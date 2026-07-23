---
name: branch-and-commit
description: Automates checking out a new feature branch and creating a conventional commit.
allowed-tools: [execute_bash]
---

# Branch & Commit Workflow

## Step 1: Check Environment

- Verify if you are on the default branch (master).
- Run `git status` to make sure there are uncommitted changes.

## Step 2: Name & Create the Branch

- Branch names follow a sequential ticket-style convention: `SC-<NN>/<short-kebab-case-description>` (e.g., `SC-01/project-foundation-setup`).
- Determine `<NN>` by finding the highest existing `SC-NN` number across local and remote branches (`git branch -a`) and incrementing by one, zero-padded to 2 digits. Start at `SC-01` if none exist.
- The description segment uses lowercase letters and hyphens only, kept short and based on the changes being committed.
- Run `git checkout -b <branch-name>` to create and switch to it.

## Step 3: Stage and Commit Changes

- Run `git add -A` to stage the changes.
- Generate a short commit message summarizing the change, formatted as `SC-NN - message` using the same `NN` from the branch name (e.g., `SC-01 - project foundation setup`).
- Run `git commit -m "<message>"`.
