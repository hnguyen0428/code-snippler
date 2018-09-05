import React, { Component } from 'react';
import PropTypes from 'prop-types';

import ChevronRight from '@material-ui/icons/ChevronRight';
import ChevronLeft from '@material-ui/icons/ChevronLeft';

import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import Toolbar from '@material-ui/core/Toolbar';

import {styles} from './styles';


class Paginator extends Component {
    render() {
        let style = styles.rootCtn;
        if (this.props.style)
            style = {...style, ...this.props.style};

        const leftDisabled = this.props.leftDisabled !== undefined ? this.props.leftDisabled : false;
        const rightDisabled = this.props.rightDisabled !== undefined ? this.props.rightDisabled : false;

        return (
            <Toolbar style={style}>
                <IconButton onClick={this.props.onLeftIconClick} disabled={leftDisabled || this.props.page === 1}>
                    <ChevronLeft/>
                </IconButton>

                { this.props.maxPage ?
                    <InputLabel>{this.props.page} of {this.props.maxPage}</InputLabel>
                    :
                    <InputLabel>{this.props.page}</InputLabel>
                }

                <IconButton onClick={this.props.onRightIconClick} disabled={rightDisabled || this.props.page === this.props.maxPage}>
                    <ChevronRight/>
                </IconButton>
            </Toolbar>
        );
    }
}


Paginator.propTypes = {
    onLeftIconClick: PropTypes.func,
    onRightIconClick: PropTypes.func,
    page: PropTypes.string.isRequired,
    leftDisabled: PropTypes.bool,
    rightDisabled: PropTypes.bool,
    style: PropTypes.object,
    maxPage: PropTypes.number
};


export default Paginator;
