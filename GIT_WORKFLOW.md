# Git(hub) Workflow

This document is based on a guide written by @goto-bus-stop and on the Github Forking Guide

This document is both a quick introduction to some command-line Git & a
description of how we'll use Git and Github for this project specifically.

It's quite strict, and in reality we'll be a bit more loose. This just describes
a "best-case" scenario.

## Forking
Since you cannot work directly on this repository you need to "clone" it to your own GitHub profile. This process is making a Fork.

To fork this repo click the fork icon on the right hand top of the repository.

![Fork.png](fork.png)

After this you have a copy on your own GitHub profile. Since you cannot work directly on GitHub you need to make a local copy(clone) of your forked repository. To do this copy the link of your forked repository of GitHub and exectute the following:

```bash
git clone [your copied link]
```

now you have a local copy of your forked repository.

One important thing needs to be added before you can really start programming and committing. If changes and updates are added to the original repository it will not automatically sync those changes to your fork. For this you need to add an extra link to the original repository to your local clone.

To add the original repository as remote(extra link):

```bash
git remote add upstream https://github.com/IcyPalm/Hanze-TwoPlayerGameServer.git
```

Now you can update your local repositories master branch with the master branch of the original repository.

```bash
git fetch upstream
git checkout master
git merge upstream/master
```

Since we assume you are not working on your master branch nothing should go wrong and a fast-forward should take place. 

## Making Changes

Before you make _any_ change, branch off `master`. Try _not_ to branch off other
branches, because they might get rebased or rewritten, and if that happens your
branch will get super difficult to merge.

To branch off master:

```bash
git checkout master
git checkout -b my-epic-feature # "-b" creates a new branch
```

Then commit your changes on the `my-epic-feature-branch`. Try to keep commits
_small_ and _UNIXy_. Essentially, don't commit a bunch of stuff that does a
lot of different things, but make multiple small commits that do simple, small
things.

```bash
# "-p" allows you to review the changes while adding them, and it also allows
# you to commit only some parts of a file.
git add -p changed/file.java
# "-m" allows you to set a commit message quickly.
git commit -m "Fix rendering of corner board squares"
# If you need more than say, 70 or 80 characters to explain your commit, leave
# off the "-m", and Git will open an editor for your commit message.
# Describe your commit briefly on the first line, in 70-80 characters max, and
# add a more elaborate explanation after two newlines.
git commit
# Example message:
# > Improve Reversi board scoring algorithm
# >
# > Previously, corner squares were scored at the same value as centre squares.
# > However, corners are very useful, so they deserve a higher score!
```

Before committing, it's a good idea to test your code. In Your Favourite IDE™,
there's probably a button for it! Otherwise:

```bash
make test
```

When you're mostly done with your branch, push it to Github using:

```bash
git push origin my-epic-feature # of course, insert your own branch name
```

Then, open your branch in Github and press the green Pull Request button. This
will allow other project members to review your code. They'll merge it when
they're done.

Also, Travis-CI will run the tests once again, and will prevent other project
members from merging the request if the tests do not pass.

## Rebasing

If other branches have been merged to master while you were working on your
branch, you might need to rebase your branch.

```bash
git rebase master my-epic-feature # insert your own branch name
```

You might run into merge conflicts here. If you're not sure what to do at that
point, poke [@IcyPalm](https://github.com/IcyPalm) or [@goto-bus-stop](https://github.com/goto-bus-stop),
because resolving conflicts is much easier to explain IRL. Or you can ask
[DuckDuckGo](https://duckduckgo.com/?q=resolving+merge+conflicts) if you like.

After a rebase, your commit history will be rewritten. That means that you can't
push your branch to Github as you're used to. Instead, you have to force-push
it, _overwriting_ the commit history on Github.

**ONLY EVER DO THIS FOR YOUR OWN BRANCHES.** Every time someone force-pushes
to master, a kitten dies. Don't be mean.

```bash
git push origin -f my-epic-feature
```
