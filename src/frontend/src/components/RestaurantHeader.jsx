import './RestaurantHeader.css';
import { formatRestaurantStatus } from '../app/formatRestaurantStatus';
import { getPreperationQueueSize} from '../app/api';
import {useEffect, useState} from 'react';

function RestaurantHeader({name, address, status}) {
  const statusLabel = formatRestaurantStatus(status);
      const [queueSize, setQueueSize] = useState(0);

    useEffect(() => {
        getPreperationQueueSize()
        .then((size) => {
            setQueueSize(size);
        })
        .catch(() => {
            setQueueSize(0);
        })
    })

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
        { queueSize > 5 ? (
          <p className="long-time">Long wait time</p>
          ): queueSize > 2 ? (
            <p className="building-time">Building wait time</p>
          ): queueSize >= 0 ? (
            <p className="short-time">Short wait time</p>
          ): <p>{queueSize}</p>}
    </div>
  );
}

export default RestaurantHeader;