import React, { Component } from 'react';
import {connect} from 'react-redux';

import SnippetsList from '../../dumb/SnippetsList/SnippetsList';

import InputLabel from '@material-ui/core/InputLabel';
import Paginator from '../../dumb/Paginator/Paginator';

import SnipplerConfig from '../../../constants/SnipplerConfig';

import {searchSnippets} from '../../../redux/actions/snippetActions';

import {styles} from './styles';

class SearchPage extends Component {
    constructor(props) {
        super(props);

        this.state = {
            paginationParams: {
                page: 0,
                pageSize: SnipplerConfig.SEARCH_PAGE_SIZE
            }
        };
    }


    handleForwardPaging = () => {
        let paginationParams = {
            page: this.state.paginationParams.page + 1,
            pageSize: this.state.paginationParams.pageSize
        };

        this.setState({
            paginationParams: paginationParams
        });

        let query = this.props.searcher.snippets.query;
        let params = {
            ...paginationParams,
            query: query
        };
        this.props.searchSnippets(params);
    };


    handleBackwardPaging = () => {
        let paginationParams = {
            page: this.state.paginationParams.page - 1,
            pageSize: this.state.paginationParams.pageSize
        };

        this.setState({
            paginationParams: paginationParams
        });

        let query = this.props.searcher.snippets.query;
        let params = {
            ...paginationParams,
            query: query
        };
        this.props.searchSnippets(params);
    };


    render() {
        let snippets = this.props.searcher.snippets.values;

        return (
            <div style={styles.rootCtn}>
                {snippets.length !== 0 ?
                    <div style={styles.contentCtn}>
                        <SnippetsList style={styles.snippetsCtn} snippets={snippets}/>

                        <Paginator
                            page={this.state.paginationParams.page + 1}
                            onLeftIconClick={this.handleBackwardPaging}
                            onRightIconClick={this.handleForwardPaging}
                            leftDisabled={this.state.paginationParams.page === 0}
                            rightDisabled={snippets.length === 0}
                        />
                    </div>
                    :
                    <div style={styles.emptyContentCtn}>
                        <div style={styles.emptySnippetMessageCtn}>
                            <InputLabel style={styles.noSnippetMessage}>There are no snippets here</InputLabel>
                        </div>
                        <Paginator
                            page={this.state.paginationParams.page + 1}
                            onLeftIconClick={this.handleBackwardPaging}
                            onRightIconClick={this.handleForwardPaging}
                            leftDisabled={this.state.paginationParams.page === 0}
                            rightDisabled={snippets.length === 0}
                        />
                    </div>
                }
            </div>
        );
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth,
        snippets: state.snippets,
        searcher: state.searcher
    };
}


export default connect(mapStateToProps, {searchSnippets})(SearchPage);