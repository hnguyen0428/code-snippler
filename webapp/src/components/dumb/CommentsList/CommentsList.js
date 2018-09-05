import React, { Component } from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';

import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import CommentItem from '../../smart/CommentItem/CommentItem';
import CircularProgress from '@material-ui/core/CircularProgress';

import {styles} from './styles';


class CommentsList extends Component {
    constructor(props) {
        super(props);
    }


    isBottom = () => {
        let list = document.getElementById("commentsList");
        let bottom = document.getElementById("commentsListBottom");
        return bottom.getBoundingClientRect().bottom <= list.getBoundingClientRect().bottom;
    };


    trackScrolling = (event) => {
        if (this.isBottom() && this.props.onScrollToBottom) {
            this.props.onScrollToBottom(this);
        }
    };


    render() {
        const {comments, ...props} = this.props;

        return (
            <List id="commentsList" {...props} onScroll={this.trackScrolling}>
                {
                    comments.map((comment, i) => {
                        const id = i === comments.length - 1 ? "commentsListBottom" : undefined;
                        return (
                            <CommentItem id={id} comment={comment} style={styles.commentItem}/>
                        );
                    })
                }
                {
                    this.props.loading &&
                        <div style={styles.progressCtn}>
                            <CircularProgress size={40}/>
                        </div>
                }
            </List>
        );
    }
}


CommentsList.propTypes = {
    onScrollToBottom: PropTypes.func,
    comments: PropTypes.array.isRequired,
    loading: PropTypes.bool
};


CommentsList.defaultProps = {
    loading: false
};



export default CommentsList;