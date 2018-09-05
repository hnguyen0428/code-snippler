import Auth from './services/Auth';
import Snippet from './services/Snippet';
import User from './services/User';
import Language from './services/Language';
import Comment from './services/Comment';


class SnipplerClient {
    constructor(config) {
        this.config = {
            baseUrl: config.baseUrl,
            clientKey: config.clientKey,
            apiKey: config.apiKey,
            languagesFilter: config.languagesFilter ? config.languagesFilter : []
        };
    }

    setApiKey(key) {
        this.config.apiKey = key;
    }

    setClientKey(key) {
        this.config.clientKey = key;
    }

    auth() {
        return new Auth(this.config);
    }

    snippet() {
        return new Snippet(this.config);
    }

    user() {
        return new User(this.config);
    }

    language() {
        return new Language(this.config);
    }

    comment() {
        return new Comment(this.config);
    }

    addToLanguagesFilter(language) {
        this.config.languagesFilter.push(language);
        localStorage.setItem("languagesFilter", this.config.languagesFilter.join(","));
    }

    removeFromLanguagesFilter(language) {
        let index = this.config.languagesFilter.indexOf(language);
        if (index !== -1) {
            this.config.languagesFilter.splice(index, 1);
            localStorage.setItem("languagesFilter", this.config.languagesFilter.join(","));
        }
    }

    resetLanguagesFilter() {
        this.config.languagesFilter = [];
        localStorage.removeItem("languagesFilter");
    }

    constructLanguagesFilter() {
        return this.config.languagesFilter.length !== 0 ? this.config.languagesFilter.join(",") : "all"
    }

    getLanguagesFilter() {
        return this.config.languagesFilter;
    }
}

const languagesFilterStr = localStorage.getItem("languagesFilter");
let languagesFilter = null;
if (languagesFilterStr)
    languagesFilter = languagesFilterStr.split(",");

const client = new SnipplerClient({
    baseUrl: 'http://localhost:8080',
    clientKey: 'vEysoE5HxX_rRADv1BDJ_v19KoKun6x49p4rq3ZYVxxTtYFc9r-beDh--a1Y8E5GbCpOKEHgJhCvsrnrtbFRomI8TNRNEyStBzUe6UUtgc9gsKBV8bDf6O71j6mk_WA-nYrD-AHHIhf3RI8rls7vNHmQLyPHxA2CDEjiDEkMhYA',
    languagesFilter: languagesFilter
});


export default client;
