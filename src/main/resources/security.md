1. realm - a virtual isolated space with specific security rules, roles that have different rights in it, 
and it is separate from other realms that may have different rules and settings.
2. client - a service that relies on keycloak to handle authentication or authorization for it.
3. role - set of the rules that define the rights of the users

JWT token

1.how it is trusted
- the jwt token hashes the header + payload + secret key and provides the algorithm that it used to hash, this way it
  signs the token and protects from any unexpected changes by somebody else. If the signature matches to the expected
  one authorization succeeds else it fails.

2.standart claims
- are the standard fields that the token carries with it.

3.expiry/refresh behavior
- When a client authenticates to a service he receives two types of tokens: refresh tokens and access token. Access
  tokens are very short-lived and have expiry time from 1-2 min up to 60. After the time is up they are no longer valid.
  So if they one gets stolen they will expire soon. Same with the refresh token, after every request for access token
  the client may also send request for new refresh token. It protects you if somebody else steels your refresh token so
  it becomes invalid after being refreshed

4.realm roles and client roles
- realm roles are the broad specter of abilities of the role while the client roles are app specific permissions 