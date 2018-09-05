import {combineReducers} from 'redux';
import routerReducer from './routerReducer';
import {routerReducer as reactRouterReducer} from 'react-router-redux';
import authReducer from './authReducer';
import userReducer from './userReducer';
import snippetReducer from './snippetReducer';
import commentReducer from './commentReducer';
import languageReducer from './languageReducer';
import alertReducer from './alertReducer';
import settingsReducer from './settingsReducer';
import searchReducer from './searchReducer';


export default combineReducers({
    auth: authReducer,
    users: userReducer,
    snippets: snippetReducer,
    comments: commentReducer,
    languages: languageReducer,
    router: routerReducer,
    alert: alertReducer,
    routing: reactRouterReducer,
    settings: settingsReducer,
    searcher: searchReducer
});