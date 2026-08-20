# Level 6 UI Test Plan

## Test protocol

- Main class: `Stephen`
- Java version: 25
- Compile destination: `out/ui-test`
- Machine-readable cases: `test/ui-test-cases.json`
- Each test case starts a fresh process. Its inputs are sent in order and the entire console output is compared exactly, except for platform line endings and one trailing newline.
- Testing stops immediately after the first failed test case. The transcript shows console inputs with a `>` prefix, followed by the actual console output. A failure also shows the exact expected output.
- The separator emitted by the program is a blank line followed by 48 underscore characters.

## LEVEL4-01 — Add a todo

**Aim:** Verify that a todo is added and displayed with the todo type and incomplete status.

**Inputs:**

1. `todo read book`
2. `bye`

**Expected output:**

```text
Hello! I'm Stephen.
What can I do for you?

________________________________________________

________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.

________________________________________________

________________________________________________
Bye. Hope to see you again soon!

________________________________________________
```

## LEVEL4-02 — Add a deadline

**Aim:** Verify that a deadline is added with its description and date string.

**Inputs:** `deadline return book /by Sunday`, then `bye`.

**Expected output:** Same greeting, separators, count, and farewell as LEVEL4-01; the added task line is `[D][ ] return book (by: Sunday)`.

## LEVEL4-03 — Add an event

**Aim:** Verify that an event is added with its description and time string.

**Inputs:** `event project meeting /from Mon 2pm /to 4pm`, then `bye`.

**Expected output:** Same greeting, separators, count, and farewell as LEVEL4-01; the added task line is `[E][ ] project meeting (from: Mon 2pm to: 4pm)`.

## LEVEL4-04 — List all three task types

**Aim:** Verify that `list` preserves insertion order and displays todo, deadline, and event formats.

**Inputs:** Add `todo read book`, `deadline return book /by Sunday`, and `event project meeting /from Mon 2pm /to 4pm`; enter `list`, then `bye`.

**Expected output:** The three normal add confirmations have counts 1, 2, and 3. The list section is exactly:

```text
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

The normal greeting, separators, and farewell surround the interactions as recorded exactly in `test/ui-test-cases.json`.

## LEVEL4-05 — Mark a task

**Aim:** Verify that `mark 1` changes a todo's status from incomplete to complete.

**Inputs:** Add `todo read book`; enter `mark 1`, then `bye`.

**Expected output:** After the normal add response, the mark response is exactly:

```text
Nice! I've marked this task as done:
[T][X] read book
```

## LEVEL4-06 — Unmark a task

**Aim:** Verify that `unmark 1` changes a previously marked todo back to incomplete.

**Inputs:** Add `todo read book`; enter `mark 1`, `unmark 1`, then `bye`.

**Expected output:** After the add and mark responses, the unmark response is exactly:

```text
OK, I've marked this task as not done yet:
[T][ ] read book
```

## LEVEL4-07 — Accept an arbitrary deadline date

**Aim:** Verify that deadline dates are stored as arbitrary strings rather than parsed as a fixed date format.

**Inputs:** `deadline submit report /by sometime after lunch-ish`, then `bye`.

**Expected output:** The added task is `[D][ ] submit report (by: sometime after lunch-ish)`; all surrounding output matches the standard add session.

## LEVEL4-08 — Accept arbitrary event dates

**Aim:** Verify that event start and end values are stored as arbitrary strings rather than parsed as fixed date formats.

**Inputs:** `event festival /from when the gates open /to after the encore`, then `bye`.

**Expected output:** The added task is `[E][ ] festival (from: when the gates open to: after the encore)`; all surrounding output matches the standard add session.

## LEVEL5-01 — Handle invalid input and preserve state

**Aim:** Verify that malformed commands show specific errors, do not terminate the chatbot,
and do not change task state. Valid commands are interleaved with invalid commands.

**Inputs:** Invalid and valid forms of `todo`, `deadline`, `event`, `mark`, `unmark`,
and `delete`, followed by the unknown command `blah`, `list`, and `bye`. The exact sequence
is recorded in `test/ui-test-cases.json` and includes missing descriptions and delimiters,
empty date/time values, missing/non-numeric/zero/negative/out-of-range task numbers, and
successful add, mark, unmark, and delete operations.

**Expected output:** Every invalid command prints an `Oops!` message and the next command is
still processed. The final list contains only the two valid tasks that were not deleted:

```text
Here are the tasks in your list:
1.[D][ ] return book (by: Sunday)
2.[E][ ] meeting (from: 2pm to: 4pm)
```

## LEVEL6-01 — Delete tasks and preserve state after invalid deletes

**Aim:** Verify deletion at every list position, automatic renumbering, deletion until the
list is empty, and preservation of list state after rejected delete commands.

**Inputs:** Add three todos; try `delete`, `delete abc`, `delete 0`, `delete -1`, and
`delete 999`; list the tasks; delete task 2 and list; then delete task 1 twice, listing
after each deletion; finally enter `bye`.

**Expected output:** Each invalid command prints the existing specific task-number error,
and the first list still contains all three tasks. Deleting task 2 removes `second task`
and renumbers `third task` from 3 to 2. The following deletions remove the first task and
then the remaining (last) task. The final list has no numbered task lines. Exact output is
recorded in `test/ui-test-cases.json`.
