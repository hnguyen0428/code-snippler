import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import CircularProgress from '@material-ui/core/CircularProgress'
import SnippetItem from '../../smart/SnippetItem/SnippetItem';

import {styles} from './styles';


class SnippetsList extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {

    }

    renderSnippet(snippet) {
        return (
            <SnippetItem style={styles.snippetItem} snippetId={snippet.snippetId}/>
        );
    }

    render() {
        let snippets = this.props.snippets;

        if (this.props.loading) {
            return (
                <div style={styles.progressCtn}>
                    <CircularProgress size={50}/>
                </div>
            );
        }
        else {
            return (
                <List style={this.props.style}>{snippets.map(this.renderSnippet)}</List>
            );
        }
    }
}


SnippetsList.propTypes = {
    snippets: PropTypes.array.isRequired,
    loading: PropTypes.bool
};



SnippetsList.defaultProps = {
    loading: false
};


export default SnippetsList;