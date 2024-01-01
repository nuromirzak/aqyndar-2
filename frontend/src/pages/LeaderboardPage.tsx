import {useLoaderData} from "react-router-dom";
import {LeaderboardLoaderResponse} from "../types";
import LeaderboardComponent from "../components/Leaderboard.tsx";

export default function LeaderboardPage() {
    const {poems, users} = useLoaderData() as
        LeaderboardLoaderResponse;

    return (
        <div className="container-fluid d-flex flex-row justify-content-around">
            <LeaderboardComponent topEntities={poems} title='Top Poems'/>
            <LeaderboardComponent topEntities={users} title='Top Users'/>
        </div>
    );
}