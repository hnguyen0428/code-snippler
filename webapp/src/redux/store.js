import {createStore, applyMiddleware, combineReducers} from 'redux';
import {syncHistoryWithStore, routerMiddleware, routerReducer, push} from 'react-router-redux';
import history from '../root/history';
import thunk from 'redux-thunk';
import rootReducer from './reducers/reducers';

const initialState = {};

const middleware = [thunk, routerMiddleware(history)];

const store = createStore(rootReducer, initialState, applyMiddleware(...middleware));

const storeHistory = syncHistoryWithStore(history, store);

export default store;