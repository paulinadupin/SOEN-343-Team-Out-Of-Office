import { useEffect, useState } from 'react'
import { getEntries, subscribe, isConsoleOpen, subscribeVisibility } from '../app/consoleLog'
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
