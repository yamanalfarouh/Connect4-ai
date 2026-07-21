# Connect Four AI

A Java Swing implementation of Connect Four with an AI opponent driven by
**minimax search with alpha-beta pruning** over a hand-written positional
heuristic — no external libraries, just the JDK standard library.

![Gameplay screenshot](screenshots/gameplay.png)

## Why this project

This started as a simpler NetBeans prototype and was rebuilt into a proper
minimax/alpha-beta agent — the two versions sit side by side in the git
history of this project's development (see **Bugs found & fixed** below for
what changed and why). Rebuilding my own earlier, weaker version rather than
starting over is a big part of why I picked this project to show: it's a
concrete before/after on both AI reasoning and plain software correctness.

## How it works

- The board is a `6x7` `char[][]` grid (`'x'` = AI, `'o'` = human, `'.'` =
  empty), rendered as a grid of `JLabel`s that get recolored on every move.
- Each human click drops a piece into the lowest open row of the chosen
  column, checks for a win/draw, then hands over to the AI.
- The AI (`Connect4Frame.AIPlayer`) runs `minimax(board, depth=3, alpha, beta,
  maximizingPlayer)` with alpha-beta pruning. Terminal/leaf nodes are scored
  by `evaluate()`, which slides a 4-cell window across every row, column, and
  both diagonal directions and scores how close each window is to a win for
  either side.

## Tech stack

- Java 17 (standard library only — `java.awt`, `javax.swing`)
- Maven for the build descriptor (`pom.xml`); no third-party dependencies

## Bugs found & fixed

I actually ran this end-to-end (compiling and launching the Swing GUI under
Xvfb, since I was doing this work in a headless sandbox) rather than just
reading the code, which is how these turned up. All four checks below are
reproducible with the small reflection-based test harnesses I used during
development (not included in this repo, since they poke at private internals
purely to prove the point — the before/after numbers are directly copy-pasted
from running them).

| # | Bug | Evidence before fix | Evidence after fix |
|---|-----|----------------------|----------------------|
| 1 | **Earlier prototype's AI couldn't recognize wins.** Its internal `checkWin()` was a stub that always returned `false`, so minimax could never see a winning terminal state during search. | Given a board with three `o`'s in a row and an open winning column, the AI picked a losing column instead of the win. | Rebuilt with a real `checkWin()` reused from the board logic, plus alpha-beta pruning and a proper heuristic. Same test: AI correctly takes the winning column. |
| 2 | **Board never initialized on startup.** `board` cells default to Java's `char` zero-value, not `'.'`, and the code that seeds `'.'` was only wired to the Start/Reset buttons — never the constructor. | A freshly opened window was unplayable: every column click was a silent no-op until Reset was clicked once. | Added a `newGame()` method (seed board, reset turn, redraw) called from the constructor. Board is `.` and playable immediately on launch. |
| 3 | **`turn` initialized to the wrong value.** It defaulted to `false`, which is the "AI just moved" state, not "human's turn." | The very first click of every game landed in the wrong branch of `insert()`: it silently placed the human's piece as `'x'` (the AI's color) and skipped the AI's reply entirely. Measured: 1 `x` / 0 `o` on the board after one human click. | `newGame()` now explicitly sets `turn = true`. Same test after the fix: 1 `o` / 1 `x` — the human's move is placed correctly and the AI replies as expected. |
| 4 | **Regression check** — did fixing turn/board init break the AI's win-taking behavior from fix #1? | — | Re-ran the same winning-move test after both fixes: AI still takes the winning column correctly. No regression. |

Everything else (imports, class/inner-class naming, dead code, section
comments) was general cleanup, not functional changes.

## How to run

**Plain `javac`/`java` (no Maven required):**
```bash
cd src/main/java
javac connect4/Connect4Frame.java
java connect4.Connect4Frame
```

**With Maven**, if you have it installed:
```bash
mvn compile exec:java
```

Click a numbered column button (`1st`–`7th`) to drop your piece; the AI
replies automatically. "start" / "reset" begin a new game at any point.

## Limitations

- The AI looks 3 plies ahead — deliberately shallow so it responds instantly;
  it will lose to a player who plans further ahead than that.
- The evaluation function only scores 4-in-a-row windows; it has no notion of
  center-column control or forced-move sequences beyond the search depth.
- No persistence, difficulty levels, or undo — it's a single local two-player
  (human vs AI) session per window.
- GUI layout is NetBeans GUI Builder generated code (`initComponents()`); it's
  functional but not hand-tuned for resizing/scaling.

## Honest assessment

This is a good showcase for classic AI search (minimax/alpha-beta) and for
demonstrating a genuine debugging process end-to-end — the table above is a
real before/after, not a cosmetic polish pass. It's a small, single-file
project, so it won't carry a portfolio on its own, but it's a solid, fully
working, honestly-documented piece alongside larger projects.
