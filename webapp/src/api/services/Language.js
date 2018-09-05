import SnipplerService from "./SnipplerService";


const ENDPOINTS = {
    getAllLanguages: {
        value: '/api/language/all',
        method: 'get'
    }
};


export default class Language extends SnipplerService {
    constructor(config) {
        super(config);
    }


    getAllLanguages(callback) {
        let config = {
            url: this.formFullUrl(this.baseUrl, ENDPOINTS.getAllLanguages.value),
            method: ENDPOINTS.getAllLanguages.method,
        };

        this.request(config, function(response, error) {
            SnipplerService.handleRequestResult(response, error, callback);
        });
    }
}