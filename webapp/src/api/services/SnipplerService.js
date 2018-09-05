let axios = require('axios');


export default class SnipplerService {
    constructor(config) {
        this.baseUrl = config.baseUrl;
        this.apiKey = config.apiKey;
        this.clientKey = config.clientKey;
        this.headers = {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Client-Key": config.clientKey
        };
    }


    setApiKey() {
        if (this.apiKey)
            this.headers['Authorization'] = this.apiKey;
    }


    formFullUrl(baseUrl, endpoint, pathParams) {
        for (let key in pathParams) {
            endpoint = endpoint.replace(`{${key}}`, pathParams[key]);
        }
        return baseUrl + endpoint;
    }


    request(config, callback) {
        this.setApiKey();

        // Overwrite the default headers if the config passed in has headers defined
        config.headers = config.headers || this.headers;

        // Use form data for post patch put requests
        if (config.params) {
            if (config.method === 'post') {
                let formData = new FormData();
                for (let key in config.params)
                    formData.set(key, config.params[key]);

                delete config['params'];
                config.data = formData;
            }
        }


        axios(config)
            .then(function(response) {
                callback(response, null);
            })
            .catch(function(error) {
                callback(null, error);
            });
    }


    static handleRequestResult(response, error, callback) {
        if (response) {
            if (callback)
                callback(response.data, null);
        }
        else {
            console.log(error.response);
            if (callback) {
                callback(null, error);
            }
        }
    }
}