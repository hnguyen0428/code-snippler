# CodeSnippler Backend

## Language
### GET: /api/language/all
Get all languages supported by the application

Example Response:

    {
        "data": <array of language objects>,
        "success": true
    }


## User
### POST: /api/user/register
Registers an account

Request Parameters:

    {
        "username": <string>,
        "password": <string>
    }

Example Response:

    {
        "data": {
            "username": <string>,
            "apiKey": <string>,
            "savedSnippets": <array of snippet ids or objects>,
            "createdSnippets": <array of snippet ids or objects>,
            "createdDate": <string>           
        },
        "success": true
    }
    
    
### POST: /api/user/login
Log into an account

Request Parameters:

    {
        "username": <string>,
        "password": <string>
    }
    
Example Response:

    {
        "data": {
            "userId": <string>,
            "username": <string>,
            "savedSnippets": <array of snippet ids or objects>,
            "createdSnippets": <array of snippet ids or objects>,
            "createdDate": <string>,
            "apiKey": <string>
        },
        "success": true
    }


### GET: /api/user/me, needs Authorization
Get the currently logged in user, which is determined by the apiKey sent in the request headers

Request Parameters:

    {
        "showSnippetDetails": <boolean> (optional) (if true, snippet objects will also be queried)
    }
    
Example Response:

    {
        "data": {
            "userId": <string>,
            "username": <string>,
            "savedSnippets": <array of snippet ids or objects>,
            "createdSnippets": <array of snippet ids or objects>,
            "createdDate": <string>"
        },
        "success": true
    }
    
    
### GET: /api/user/{userId}
Get information of the user specified by the userId

Request Parameters:

    {
        "showSnippetDetails": <boolean> (optional) (if true, snippet objects will also be queried)
    }
    
Example Response:

    {
        "data": {
            "userId": <string>,
            "username": <string>,
            "savedSnippets": <array of snippet ids or objects>,
            "createdSnippets": <array of snippet ids or objects>,
            "createdDate": <string>"
        },
        "success": true
    }
    
    
### GET: /api/user/byIds
Get information of the users specified by the array of user ids

Request Parameters:

    {
        "ids": <array of user ids>
    }
    
Example Response:

    {
        "data": <array of user objects>,
        "success": true
    }
    

### GET: /api/user/savedSnippets, needs Authorization
Get all of the logged in user's saved snippets
    
Example Response:
    
    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/user/{userId}/savedSnippets
Get all of the user's saved snippets
    
Example Response:
    
    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/user/createdSnippets, needs Authorization
Get all of the logged in user's created snippets
    
Example Response:
    
    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/user/{userId}/createdSnippets
Get all of the user's created snippets
    
Example Response:
    
    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
## CodeSnippet
### POST: /api/snippet, needs Authorization
Create a code snippet

Request Parameters:

    {
        "title": <string>,
        "description": <string>,
        "code": <string>,
        "language": <string>
    }
    
Example Response:

    {
        "data": {
            "snippetId": <string>,
            "title": <string>,
            "description": <string>,
            "code": <string>,
            "userId": <string>,
            "languageName": <string>,
            "viewsCount": <int>,
            "upvotes": <int>,
            "downvotes": <int>,
            "savedCount": <int>,
            "comments": <array>,
            "createdDate": <string>
        },
        "success": true
    }
    

### PATCH: /api/snippet/{snippetId}, needs Authorization
Update a code snippet

Request Parameters:

    {
        "title": <string>,
        "description": <string>,
        "code": <string>,
        "language": <string>
    }
    
Example Response:

    {
        "data": {
            "snippetId": <string>,
            "title": <string>,
            "description": <string>,
            "code": <string>,
            "userId": <string>,
            "languageName": <string>,
            "viewsCount": <int>,
            "upvotes": <int>,
            "downvotes": <int>,
            "savedCount": <int>,
            "comments": <array>,
            "createdDate": <string>
        },
        "success": true
    }


### DELETE: /api/snippet/{snippetId}, needs Authorization
Delete a code snippet
    
Example Response:

    {
        "success": true
    }
    

### GET: /api/snippet/{snippetId}
Get a code snippet

Request Parameters:

    {
        "increaseViewcount": <boolean> (if true, the view count will increase),
        "showCommentDetails": <boolean> (if true, comment objects will be queried),
        "showUserDetails": <boolean> (if true, user object will be queried)
    }
    
Example Response:

    {
        "data": {
            "snippetId": <string>,
            "title": <string>,
            "description": <string>,
            "code": <string>,
            "userId": <string>,
            "languageName": <string>,
            "viewsCount": <int>,
            "upvotes": <int>,
            "downvotes": <int>,
            "savedCount": <int>,
            "comments": <array of comment ids or objects>,
            "popularityScore": <int>,
            "upvoted": <boolean> (indicates if user has upvoted this snippet, only shows if Authorization is provided),
            "downvoted": <boolean> (indicates if user has downvoted this snippet, only shows if Authorization is provided),
            "saved": <boolean> (indicates if user has saved this snippet, only shows if Authorization is provided),
            "createdDate": <string>
        },
        "success": true
    }
    
    
### GET: /api/snippet/byIds
Get code snippets by their ids

Request Parameters:

    {
        "ids": <array of snippet ids>,
        "showUserDetails": <boolean> (if true, user objects will be queried)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/snippet/byLanguage
Get code snippets by a language

Request Parameters:

    {
        "ids": <array of snippet ids>,
        "language": <language name>
        "page": <int> (page number),
        "pageSize": <int> (how many to query),
        "fields": <string> (attribute names separated by commas, possible values are title|description|code|upvotes|downvotes|viewsCount|savedCount|languageName|userId|createdDate|comments)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### PATCH: /api/snippet/{snippetId}/upvote, needs Authorization
Upvote a code snippet

Request Parameters:

    {
        "upvote": <boolean>
    }
    
Example Response:

    {
        "success": true
    }


### PATCH: /api/snippet/{snippetId}/downvote, needs Authorization
Downvote a code snippet

Request Parameters:

    {
        "downvote": <boolean>
    }
    
Example Response:

    {
        "success": true
    }
    
    
### PATCH: /api/snippet/{snippetId}/save, needs Authorization
Save a code snippet

Request Parameters:

    {
        "save": <boolean>
    }
    
Example Response:

    {
        "success": true
    }
    
    
### POST: /api/snippet/{snippetId}/comment, needs Authorization
Create a comment on the snippet

Request Parameters:

    {
        "content": <string>
    }
    
Example Response:

    {
        "data": {
            "commentId": <string>,
            "content": <string>,
            "userId": <string>,
            "snippetId": <string>,
            "upvotes": <int>,
            "downvotes": <int>,
            "createdDate": <string>
        },
        "success": true
    }
    

### GET: /api/snippet/{snippetId}/comments
Get snippet's comments

Request Parameters:

    {
        "showUserDetails": <boolean> (if true, user objects are queried),
        "page": <int> (page number),
        "pageSize": <int> (how many to query)
    }
    
Example Response:

    {
        "data": <array of comment objects>,
        "success": true
    }
    
    
### GET: /api/snippet/search
Search for snippets

Request Parameters:

    {
        "query": <string>,
        "page": <int> (page number),
        "pageSize": <int> (how many to query),
        "fields": <string> (attribute names separated by commas, possible values are title|description|code|upvotes|downvotes|viewsCount|savedCount|languageName|userId|createdDate|comments)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/snippet/popular
Get the most popular snippets.

Request Parameters:

    {
        "page": <int> (page number),
        "pageSize": <int> (how many to query),
        "fields": <string> (attribute names separated by commas, possible values are title|description|code|upvotes|downvotes|viewsCount|savedCount|languageName|userId|createdDate|comments)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/snippet/mostViews
Get the snippets with the most views.

Request Parameters:

    {
        "page": <int> (page number),
        "pageSize": <int> (how many to query)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/snippet/mostUpvotes
Get the snippets with the most upvotes.

Request Parameters:

    {
        "page": <int> (page number),
        "pageSize": <int> (how many to query)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
### GET: /api/snippet/mostSaved
Get the snippets with the most saves.

Request Parameters:

    {
        "page": <int> (page number),
        "pageSize": <int> (how many to query)
    }
    
Example Response:

    {
        "data": <array of snippet objects>,
        "success": true
    }
    
    
## GET: /api/comment/byIds
Get comments

Request Parameters:

    {
        "ids": <array of snippet ids>,
        "showUserDetails": <boolean> (if true, user objects will be queried)
    }
    
    
Example Response:

    {
        "data": <array of comment objects>,
        "success": true
    }
    
    
### PATCH: /api/comment/{commentId}, need Authorization
Update a comment

Request Parameters:

    {
        "content": <string>
    }
    
Example Response:

    {
        "data": {
            "commentId": <string>,
            "content": <string>,
            "userId": <string>,
            "snippetId": <string>,
            "upvotes": <int>,
            "downvotes": <int>,
            "createdDate": <string>
        },
        "success": true
    }
    
    
### DELETE: /api/comment/{commentId}, need Authorization
Delete a comment

Example Response:

    {
        "success": true
    }
    

### PATCH: /api/comment/{commentId}/upvote, needs Authorization
Upvote a comment

Request Parameters:

    {
        "upvote": <boolean>
    }
    
Example Response:

    {
        "success": true
    }


### PATCH: /api/comment/{commentId}/downvote, needs Authorization
Downvote a comment

Request Parameters:

    {
        "downvote": <boolean>
    }
    
Example Response:

    {
        "success": true
    }