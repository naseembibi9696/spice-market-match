package com.spicemarket.match.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.domain.model.Jar
import com.spicemarket.match.domain.model.OrderLine
import com.spicemarket.match.domain.model.Spice
import com.spicemarket.match.domain.repository.ProgressRepository
import com.spicemarket.match.domain.repository.SettingsRepository
import com.spicemarket.match.domain.usecase.ApplySwapUseCase
import com.spicemarket.match.domain.usecase.BuildStallOrderUseCase
import com.spicemarket.match.domain.usecase.FindHintUseCase
import com.spicemarket.match.domain.usecase.FindMatchesUseCase
import com.spicemarket.match.domain.usecase.GenerateBoardUseCase
import com.spicemarket.match.domain.usecase.ResolveCascadeUseCase
import com.spicemarket.match.domain.usecase.ScoreRoundUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.random.Random

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val buildStallOrder: BuildStallOrderUseCase,
    private val generateBoard: GenerateBoardUseCase,
    private val findMatches: FindMatchesUseCase,
    private val applySwap: ApplySwapUseCase,
    private val findHint: FindHintUseCase,
    private val resolveCascade: ResolveCascadeUseCase,
    private val scoreRound: ScoreRoundUseCase
) : ViewModel() {

    private val state = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private val random = Random(System.currentTimeMillis())
    private val matchedThisRound = mutableMapOf<Spice, Int>()
    private var identity = 0L
    private var openedRound = -1
    private var loopJob: Job? = null
    private var hintJob: Job? = null

    fun openRound(roundId: Int) {
        if (openedRound == roundId) return
        openedRound = roundId
        deal()
    }

    fun restart() {
        deal()
    }

    private fun deal() {
        loopJob?.cancel()
        hintJob?.cancel()
        matchedThisRound.clear()
        val stall = progressRepository.progress.value.stall
        val order = buildStallOrder(stall)
        val board = generateBoard(GameConfig.COLS, GameConfig.ROWS, random) { identity++ }
        state.value = GameUiState(
            board = board,
            orderLines = order.lines,
            stall = stall,
            movesLeft = order.moves,
            score = 0,
            coinsEarned = 0,
            hintsLeft = GameConfig.HINTS_PER_LEVEL,
            phase = GamePhase.Ready,
            paused = false,
            reducedMotion = settingsRepository.settings.value.reducedMotion
        )
    }

    fun onCellTap(index: Int) {
        val current = state.value
        if (current.interactive.not() || current.paused) return
        val selected = current.selected
        if (selected == null) {
            state.value = current.copy(
                selected = index,
                phase = GamePhase.Selected,
                hinted = emptySet()
            )
            return
        }
        if (selected == index) {
            state.value = current.copy(selected = null, phase = GamePhase.Ready)
            return
        }
        if (adjacent(selected, index, current.cols).not()) {
            state.value = current.copy(selected = index, phase = GamePhase.Selected)
            return
        }
        swap(selected, index)
    }

    fun hint() {
        val current = state.value
        if (current.interactive.not() || current.hintsLeft <= 0) return
        val pair = findHint(current.board, current.cols, current.rows) ?: return
        state.value = current.copy(
            hinted = setOf(pair.first, pair.second),
            hintsLeft = current.hintsLeft - 1
        )
        hintJob?.cancel()
        hintJob = viewModelScope.launch {
            delay(GameConfig.HINT_MS)
            state.value = state.value.copy(hinted = emptySet())
        }
    }

    fun shuffle() {
        val current = state.value
        if (current.interactive.not() || current.movesLeft <= 0) return
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            state.value = state.value.copy(
                phase = GamePhase.Resolving,
                selected = null,
                hinted = emptySet(),
                movesLeft = state.value.movesLeft - 1
            )
            delay(GameConfig.SHUFFLE_MS)
            state.value = state.value.copy(
                board = generateBoard(current.cols, current.rows, random) { identity++ }
            )
            settle()
        }
    }

    fun openPause() {
        if (state.value.phase == GamePhase.Finished) return
        state.value = state.value.copy(paused = true)
    }

    fun closePause() {
        state.value = state.value.copy(paused = false)
    }

    fun pause() {
        loopJob?.cancel()
        hintJob?.cancel()
    }

    private fun swap(first: Int, second: Int) {
        val current = state.value
        val swapped = applySwap(current.board, first, second)
        if (findMatches(swapped, current.cols, current.rows).isEmpty()) {
            state.value = current.copy(
                board = swapped,
                selected = null,
                hinted = emptySet(),
                invalid = setOf(first, second),
                phase = GamePhase.Invalid
            )
            loopJob?.cancel()
            loopJob = viewModelScope.launch {
                delay(GameConfig.INVALID_MS)
                state.value = state.value.copy(
                    board = current.board,
                    invalid = emptySet(),
                    phase = GamePhase.Ready
                )
            }
            return
        }
        state.value = current.copy(
            board = swapped,
            selected = null,
            hinted = emptySet(),
            movesLeft = current.movesLeft - 1,
            phase = GamePhase.Swapping
        )
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            delay(GameConfig.SWAP_MS)
            resolve(second)
        }
    }

    private suspend fun resolve(movedIndex: Int) {
        var step = 1
        var promoted = false
        while (viewModelScope.isActive) {
            val current = state.value
            val runs = findMatches(current.board, current.cols, current.rows)
            if (runs.isEmpty()) break
            val longest = runs.maxOf { it.indices.size }
            val cleared = mutableSetOf<Int>()
            runs.forEach { cleared.addAll(it.indices) }
            var specialIndex = -1
            if (promoted.not() && longest == 4 && cleared.contains(movedIndex)) {
                specialIndex = movedIndex
                cleared.remove(movedIndex)
                promoted = true
            }
            var rowClears = 0
            val rowsToClear = cleared.filter { current.board[it].special }
            rowsToClear.forEach { index ->
                rowClears++
                val row = index / current.cols
                for (col in 0 until current.cols) cleared.add(row * current.cols + col)
            }
            state.value = state.value.copy(phase = GamePhase.Resolving, clearing = cleared)
            delay(if (current.reducedMotion) 120L else GameConfig.CLEAR_MS)

            val tally = mutableMapOf<Spice, Int>()
            cleared.forEach { index ->
                val spice = current.board[index].spice
                tally[spice] = (tally[spice] ?: 0) + 1
                matchedThisRound[spice] = (matchedThisRound[spice] ?: 0) + 1
            }
            val multiplier = min(
                GameConfig.CASCADE_CAP,
                1f + GameConfig.CASCADE_STEP * (step - 1)
            )
            var gained = cleared.size * GameConfig.COINS_PER_JAR
            if (longest == 4) gained += GameConfig.MATCH4_BONUS
            if (longest >= 5) gained += GameConfig.MATCH5_BONUS
            gained += rowClears * GameConfig.ROW_CLEAR_BONUS
            val points = (gained * multiplier).toInt()

            val updatedLines = state.value.orderLines.map { line ->
                val hit = tally[line.spice] ?: 0
                if (hit == 0) line else line.copy(cleared = min(line.target, line.cleared + hit))
            }
            var nextBoard = resolveCascade.collapse(
                board = state.value.board,
                cleared = cleared,
                cols = current.cols,
                rows = current.rows,
                random = random,
                nextId = { identity++ }
            )
            if (specialIndex >= 0) {
                val promotedBoard = nextBoard.toMutableList()
                val jar = promotedBoard[specialIndex]
                promotedBoard[specialIndex] = Jar(jar.id, jar.spice, true)
                nextBoard = promotedBoard
            }
            state.value = state.value.copy(
                board = nextBoard,
                clearing = emptySet(),
                orderLines = updatedLines,
                score = state.value.score + points,
                coinsEarned = state.value.coinsEarned + points
            )
            delay(if (current.reducedMotion) 120L else GameConfig.FALL_MS)
            step++
        }
        settle()
    }

    private suspend fun settle() {
        val current = state.value
        val win = current.orderLines.isNotEmpty() && current.orderLines.all { it.filled }
        if (win || current.movesLeft <= 0) {
            finish(win, current.orderLines)
            return
        }
        if (findHint.hasLegalMove(current.board, current.cols, current.rows).not()) {
            state.value = current.copy(phase = GamePhase.Resolving)
            delay(GameConfig.SHUFFLE_MS)
            state.value = state.value.copy(
                board = generateBoard(current.cols, current.rows, random) { identity++ }
            )
        }
        state.value = state.value.copy(phase = GamePhase.Ready, selected = null)
    }

    private suspend fun finish(win: Boolean, lines: List<OrderLine>) {
        val current = state.value
        state.value = current.copy(phase = GamePhase.Finished, selected = null, paused = false)
        val result = scoreRound(
            stall = current.stall,
            win = win,
            score = current.score,
            coinsEarned = current.coinsEarned,
            lines = lines
        )
        progressRepository.applyRound(result, matchedThisRound.toMap())
        delay(GameConfig.FINISH_DELAY_MS)
        eventChannel.send(GameEvent.RoundFinished(result))
    }

    private fun adjacent(first: Int, second: Int, cols: Int): Boolean {
        val rowFirst = first / cols
        val colFirst = first % cols
        val rowSecond = second / cols
        val colSecond = second % cols
        val rowDelta = if (rowFirst > rowSecond) rowFirst - rowSecond else rowSecond - rowFirst
        val colDelta = if (colFirst > colSecond) colFirst - colSecond else colSecond - colFirst
        return rowDelta + colDelta == 1
    }
}
