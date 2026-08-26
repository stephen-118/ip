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
`event`, `delete`, `mark`, and `unmark` command. Data is stored relative to the project
folder in `data/stephen.txt`; the `data` folder is created automatically.
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

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
