import { useEffect, useState } from 'react'
import { getEntries, subscribe } from '../app/consoleLog'
import './OutputConsole.css'

function OutputConsole() {
    const [entries, setEntries] = useState(getEntries())

    useEffect(() => {
        return subscribe(setEntries)
    }, [])

    return (
        <div className="output-console">
            <h2>Console</h2>
            <ul>
                {entries.map((entry, index) => (
                    <li key={index} className={entry.type}>
                        {entry.time.toLocaleTimeString()} · {entry.message}
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default OutputConsole
