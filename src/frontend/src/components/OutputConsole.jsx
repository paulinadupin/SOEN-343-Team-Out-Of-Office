import { useEffect, useState } from 'react'
import { getEntries, subscribe, isConsoleOpen, subscribeVisibility, toggleConsole } from '../app/consoleLog'
import './OutputConsole.css'

function OutputConsole() {
    const [entries, setEntries] = useState(getEntries())
    const [open, setOpen] = useState(isConsoleOpen())

    useEffect(() => {
        return subscribe(setEntries)
    }, [])

    useEffect(() => {
        return subscribeVisibility(setOpen)
    }, [])

    if (!open) return null

    return (
        <div className="output-console">
            <div className="output-console-header">
                <h2>Console</h2>
                <button
                    type="button"
                    className="output-console-close"
                    onClick={toggleConsole}
                    aria-label="Close console"
                >
                    &times;
                </button>
            </div>
            <ul>
                {entries.map((entry, index) => (
                    <li key={index} className={entry.type}>
                        <span className="entry-time">{entry.time.toLocaleTimeString()}</span>
                        <span className="entry-message">{entry.message}</span>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default OutputConsole
