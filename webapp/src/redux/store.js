import {applyMiddleware, createStore} from "redux";
import {routerMiddleware} from "react-router-redux";
import history from "../root/history";
import thunk from "redux-thunk";
import rootReducer from "./reducers/reducers";

const initialState = {};

const middleware = [thunk, routerMiddleware(history)];

const store = createStore(rootReducer, initialState, applyMiddleware(...middleware));

export default store;