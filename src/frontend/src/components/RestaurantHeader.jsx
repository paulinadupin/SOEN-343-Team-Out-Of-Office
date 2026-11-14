import './RestaurantHeader.css';
import { formatRestaurantStatus } from '../app/formatRestaurantStatus';
import { getPreperationQueueSize} from '../app/api';
import {useEffect, useState} from 'react';

function RestaurantHeader({name, address, status}) {
  const statusLabel = formatRestaurantStatus(status);

  return (
    <div className="restaurant-header">
      <h1 className="title">{name}</h1>
      <p className="address">{address}</p>
      <div className={`status ${statusLabel}`}>
        <svg width="19" height="19">
          <circle cx="8" cy="8" r="6"/>
        </svg>
        {statusLabel}
        </div>
    </div>
  );
}

export default RestaurantHeader;