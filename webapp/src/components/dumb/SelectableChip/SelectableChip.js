import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Chip from '@material-ui/core/Chip';
import Checkbox from '@material-ui/core/Checkbox';
import Toolbar from '@material-ui/core/Toolbar';

import CheckBox from '@material-ui/icons/CheckBox';
import CheckBoxOutlineBlank from '@material-ui/icons/CheckBoxOutlineBlank';

import {styles} from './styles';


class SelectableChip extends Component {
    onCheckBoxChange = (event, checked) => {
        this.onChipClick();
    };

    onChipClick = () => {
        if (this.props.onClick)
            this.props.onClick(this, !this.props.checked);
    };


    render() {
        return(
            <Toolbar style={styles.rootCtn}>
                <Checkbox style={{color: this.props.checkboxColor}}
                          checked={this.props.checked} onChange={this.onCheckBoxChange}
                          checkedIcon={this.props.checkedIcon}
                          icon={this.props.icon}
                />
                <Chip
                    label={this.props.label}
                    onClick={this.onChipClick}
                    style={{backgroundColor: this.props.chipColor, color: this.props.textColor}}
                />
            </Toolbar>
        );
    }
}


SelectableChip.propTypes = {
    checked: PropTypes.bool,
    label: PropTypes.string,
    onClick: PropTypes.func,
    name: PropTypes.string,
    chipColor: PropTypes.string,
    checkboxColor: PropTypes.string,
    textColor: PropTypes.string,
    checkedIcon: PropTypes.object
};

SelectableChip.defaultProps = {
    chipColor: 'white',
    checkboxColor: 'white',
    textColor: 'inherit',
    checkedIcon: <CheckBox/>,
    icon: <CheckBoxOutlineBlank/>
};


export default SelectableChip;