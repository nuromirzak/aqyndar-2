import {TopEntityResponse} from "../types";
import './styles/Leaderboard.scss';

interface LeaderboardProps {
    topEntities: TopEntityResponse[] | undefined;
    title?: string;
}

function Leaderboard({topEntities, title = 'Leaderboard'}: LeaderboardProps) {
    const displayedEntities = topEntities ? [...topEntities] : [];
    for (let i = displayedEntities.length; i < 5; i++) {
        displayedEntities.push({name: '', reactionSum: 0, id: i + 1});
    }

    return (
        <div className="leaderboard">
            <h2>{title}</h2>
            <div className="leaderboard__table">
                {displayedEntities.slice(0, 5).map((topEntity, index) => (
                    <div key={topEntity.name || index} className="leaderboard__table--row">
                        <div className="leaderboard__table-row-position">
                            {index + 1}
                        </div>
                        <div className="leaderboard__table-row-picture">
                            <img src='https://via.placeholder.com/64' width={64} height={64}
                                 alt={`Random ${index + 1}`}/>
                        </div>
                        <div className="leaderboard__table-row-name">
                            {topEntity.name || 'N/A'}
                        </div>
                        <div className="leaderboard__table-row-score">
                            {topEntity.reactionSum || 0}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Leaderboard;
