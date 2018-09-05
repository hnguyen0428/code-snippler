import SnipplerService from "./SnipplerService";
let querystring = require('querystring');


const ENDPOINTS = {
    register: {
        value: '/api/user/register',
        method: 'post'
    },
    login: {
        value: '/api/user/login',
        method: 'post'
    },
    changePassword: {
        value: '/api/user/changePassword',
        method: 'post'
    }
};


export default class Auth extends SnipplerService {
    constructor(config) {
        super(config);
    }


    register(authConfig, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.register.value),
            method: ENDPOINTS.register.method,
            params: authConfig
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    login(authConfig, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.login.value),
            method: ENDPOINTS.login.method,
            params: authConfig
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }


    changePassword(params, callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.changePassword.value),
            method: ENDPOINTS.changePassword.method,
            params: params
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }
}