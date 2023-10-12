rootProject.name = "template"

plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.10"
}

gitHooks {
    preCommit {
        tasks("checkStyleMain", "checkStyleTest", "checkStyleIntegrationTest")
    }
    commitMsg { conventionalCommits() }
    createHooks()
}
