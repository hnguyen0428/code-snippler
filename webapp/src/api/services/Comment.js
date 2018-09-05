import SnipplerService from "./SnipplerService";


const ENDPOINTS = {
    getComments: {
        value: '/api/comment/byIds',
        method: 'get'
    },
    updateComment: {
        value: '/api/comment/{commentId}',
        method: 'patch'
    },
    deleteComment: {
        value: '/api/comment/{commentId}',
        method: 'delete'
    },
    upvoteComment: {
        value: '/api/comment/{commentId}/upvote',
        method: 'patch'
    },
    downvoteComment: {
        value: '/api/comment/{commentId}/downvote',
        method: 'patch'
    }
};


export default class Comment extends SnipplerService {
    constructor(config) {
        super(config);
    }


    getComments(commentIds, params, callback) {
        params = {...commentIds, ...params};

        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getComments.value),
            method: ENDPOINTS.getComments.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    updateComment(commentId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.updateComment.value, {commentId: commentId}),
            method: ENDPOINTS.updateComment.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    deleteComment(commentId, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.deleteComment.value, {commentId: commentId}),
            method: ENDPOINTS.deleteComment.method,
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    upvoteComment(commentId, upvote, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.upvoteComment.value, {commentId: commentId}),
            method: ENDPOINTS.upvoteComment.method,
            params: {
                upvote: upvote
            }
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    downvoteComment(commentId, downvote, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.downvoteComment.value, {commentId: commentId}),
            method: ENDPOINTS.downvoteComment.method,
            params: {
                downvote: downvote
            }
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }
}