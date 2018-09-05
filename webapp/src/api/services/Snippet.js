import SnipplerService from "./SnipplerService";
import querystring from 'querystring';


const ENDPOINTS = {
    createSnippet: {
        value: '/api/snippet',
        method: 'post'
    },
    updateSnippet: {
        value: '/api/snippet/{snippetId}/update',
        method: 'post'
    },
    deleteSnippet: {
        value: '/api/snippet/{snippetId}',
        method: 'delete'
    },
    getSnippet: {
        value: '/api/snippet/{snippetId}',
        method: 'get'
    },
    getSnippets: {
        value: '/api/snippet/byIds',
        method: 'get'
    },
    getSnippetsByLanguage: {
        value: '/api/snippet/byLanguage',
        method: 'get'
    },
    upvoteSnippet: {
        value: '/api/snippet/{snippetId}/upvote',
        method: 'patch'
    },
    downvoteSnippet: {
        value: '/api/snippet/{snippetId}/downvote',
        method: 'patch'
    },
    saveSnippet: {
        value: '/api/snippet/{snippetId}/save',
        method: 'patch'
    },
    createComment: {
        value: '/api/snippet/{snippetId}/comment',
        method: 'post'
    },
    getComments: {
        value: '/api/snippet/{snippetId}/comments',
        method: 'get'
    },
    searchSnippets: {
        value: '/api/snippet/search',
        method: 'get'
    },
    getRecent: {
        value: '/api/snippet/recent',
        method: 'get'
    },
    getPopular: {
        value: '/api/snippet/popular',
        method: 'get'
    },
    getMostViews: {
        value: '/api/snippet/mostViews',
        method: 'get'
    },
    getMostUpvotes: {
        value: '/api/snippet/mostUpvotes',
        method: 'get'
    },
    getMostSaved: {
        value: '/api/snippet/mostSaved',
        method: 'get'
    }
};


export default class Snippet extends SnipplerService {
    constructor(config) {
        super(config);

        this.languagesFilter = config.languagesFilter;
    }


    createSnippet(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.createSnippet.value),
            method: ENDPOINTS.createSnippet.method,
            params: params,
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getSnippet(snippetId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getSnippet.value, {snippetId: snippetId}),
            method: ENDPOINTS.getSnippet.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getSnippets(snippetIds, params, callback) {
        params = {ids: snippetIds.join(","), ...params};

        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getSnippets.value),
            method: ENDPOINTS.getSnippets.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getSnippetsByLanguage(language, params, callback) {
        params = {language: language, ...params};

        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getSnippetsByLanguage.value),
            method: ENDPOINTS.getSnippetsByLanguage.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    updateSnippet(snippetId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.updateSnippet.value, {snippetId: snippetId}),
            method: ENDPOINTS.updateSnippet.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    deleteSnippet(snippetId, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.deleteSnippet.value, {snippetId: snippetId}),
            method: ENDPOINTS.deleteSnippet.method,
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    upvoteSnippet(snippetId, upvote, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.upvoteSnippet.value, {snippetId: snippetId}),
            method: ENDPOINTS.upvoteSnippet.method,
            params: {
                upvote: upvote
            }
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    downvoteSnippet(snippetId, downvote, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.downvoteSnippet.value, {snippetId: snippetId}),
            method: ENDPOINTS.downvoteSnippet.method,
            params: {
                downvote: downvote
            }
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    saveSnippet(snippetId, save, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.saveSnippet.value, {snippetId: snippetId}),
            method: ENDPOINTS.saveSnippet.method,
            params: {
                save: save
            }
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    createComment(snippetId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.createComment.value, {snippetId: snippetId}),
            method: ENDPOINTS.createComment.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getComments(snippetId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getComments.value, {snippetId: snippetId}),
            method: ENDPOINTS.getComments.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    searchSnippets(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.searchSnippets.value),
            method: ENDPOINTS.searchSnippets.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getRecent(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getRecent.value),
            method: ENDPOINTS.getRecent.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getPopular(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getPopular.value),
            method: ENDPOINTS.getPopular.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getMostViews(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getMostViews.value),
            method: ENDPOINTS.getMostViews.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getMostUpvotes(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getMostUpvotes.value),
            method: ENDPOINTS.getMostUpvotes.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getMostSaved(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getMostSaved.value),
            method: ENDPOINTS.getMostSaved.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }
}