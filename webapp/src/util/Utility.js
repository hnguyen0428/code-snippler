

export default class Utility {
    static withinRange(str, min, max) {
        return str.length >= min && str.length <= max;
    }

    static isAlphanum(str) {
        let regex = new RegExp(/^[a-z0-9]+$/i);
        return regex.test(str);
    }

    static validateUsername(str) {
        let regex = new RegExp(/^[a-z0-9_]+$/i);
        return regex.test(str);
    }

    static validateEmail(email) {
        return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email);
    };
}

