package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()
    var users = emptyList<User>()
    repos.forEach { repo ->
        service.getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
            .let {user ->
                users = user.plus(users)
                users = users.aggregate()
            }
          updateResults.invoke(users, false)
    }
}
