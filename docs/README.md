# Stephen User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Saving tasks

Stephen automatically saves the task list after every successful `todo`, `deadline`,
`event`, `delete`, `mark`, `unmark`, and `sort` command. Data is stored relative to the
project folder in `data/stephen.txt`; the `data` folder is created automatically.
The saved tasks are loaded on the next startup. If the folder or file is missing, Stephen
starts with an empty list. Blank or malformed records are skipped without preventing the
remaining valid tasks from loading.

Each line uses `type | completion | description | task-specific fields`. Completion is
`1` for done and `0` for not done. Pipes, backslashes, and line breaks inside values are
escaped with a backslash. For example:

```text
T | 0 | read book
D | 1 | submit report | Friday
E | 0 | project meeting | Mon 2pm | Mon 4pm
```

## Sorting tasks

Use `sort` to arrange all tasks alphabetically by their descriptions. Sorting ignores
capitalization and keeps the original order of tasks whose descriptions differ only by
letter case. The new order is saved automatically and is retained the next time Stephen
starts.

Example:

```text
sort
```

```text
I've sorted your tasks alphabetically:
1.[D][X] Alpha deadline (by: Dec 2 2019)
2.[E][ ] alpha event (from: Dec 1 2019 to: Dec 3 2019)
3.[T][ ] zebra task
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
