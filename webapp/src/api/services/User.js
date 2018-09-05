import SnipplerService from "./SnipplerService";


const ENDPOINTS = {
    updateProfile: {
        value: '/api/user/profile',
        method: 'patch'
    },
    getMe: {
        value: '/api/user/me',
        method: 'get'
    },
    getUser: {
        value: '/api/user/{userId}',
        method: 'get'
    },
    getMySavedSnippets: {
        value: '/api/user/savedSnippets',
        method: 'get'
    },
    getUserSavedSnippets: {
        value: '/api/user/{userId}/savedSnippets',
        method: 'get'
    },
    getMyCreatedSnippets: {
        value: '/api/user/createdSnippets',
        method: 'get'
    },
    getUserCreatedSnippets: {
        value: '/api/user/{userId}/createdSnippets',
        method: 'get'
    }
};


export default class User extends SnipplerService {
    constructor(config) {
        super(config);
    }


    updateProfile(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.updateProfile.value),
            method: ENDPOINTS.updateProfile.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getMe(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getMe.value),
            method: ENDPOINTS.getMe.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getUser(userId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getUser.value, {userId: userId}),
            method: ENDPOINTS.getUser.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getMySavedSnippets(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getMySavedSnippets.value),
            method: ENDPOINTS.getMySavedSnippets.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getUserSavedSnippets(userId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getUserSavedSnippets.value, {userId: userId}),
            method: ENDPOINTS.getUserSavedSnippets.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getMyCreatedSnippets(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getMyCreatedSnippets.value),
            method: ENDPOINTS.getMyCreatedSnippets.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    getUserCreatedSnippets(userId, params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getUserCreatedSnippets.value, {userId: userId}),
            method: ENDPOINTS.getUserCreatedSnippets.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }
}