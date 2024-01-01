import {createBrowserRouter, createRoutesFromElements, Route, RouterProvider} from "react-router-dom";
import RootLayout from "./components/RootLayout.tsx";
import {
    authorLoader,
    authorsLoader,
    leaderboardLoader,
    poemLoader,
    poemsLoader,
    profileLoader
} from "./router/loaders.ts";
import {createAuthorAction, createPoemAction, loginAction, registerAction} from "./router/actions.ts";
import {UserContext} from "./contexts/UserContext.tsx";
import {useContext} from "react";
import AuthorsPage from "./pages/AuthorsPage.tsx";
import GlobalErrorPage from "./pages/GlobalErrorPage.tsx";
import HomePage from "./pages/HomePage.tsx";
import PoemsPage from "./pages/PoemsPage.tsx";
import PoemPage from "./pages/PoemPage.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import ProfilePage from "./pages/ProfilePage.tsx";
import LeaderboardPage from "./pages/LeaderboardPage.tsx";
import AuthorPage from "./pages/AuthorPage.tsx";
import NotFoundPage from "./pages/NotFoundPage.tsx";
import CreateAuthorPage from "./pages/CreateAuthorPage.tsx";
import CreatePoemPage from "./pages/CreatePoemPage.tsx";


export default function App() {
    const {setUser} = useContext(UserContext);

    const router = createBrowserRouter(
        createRoutesFromElements(
            <Route path="/" element={<RootLayout/>} errorElement={<GlobalErrorPage/>}>
                <Route index element={<HomePage/>}/>
                <Route path="poems">
                    <Route index element={<PoemsPage/>} loader={poemsLoader}/>
                    <Route path=":id" element={<PoemPage/>} loader={poemLoader}/>
                    <Route path="create" element={<CreatePoemPage/>} action={createPoemAction} loader={authorsLoader}/>
                </Route>
                <Route path="login" element={<LoginPage/>} action={loginAction({setUser})}/>
                <Route path="register" element={<RegisterPage/>} action={registerAction}/>
                <Route path="profile" element={<ProfilePage/>} loader={profileLoader}/>
                <Route path="leaderboard" element={<LeaderboardPage/>} loader={leaderboardLoader}/>
                <Route path="authors">
                    <Route index element={<AuthorsPage/>} loader={authorsLoader}/>
                    <Route path=":id" element={<AuthorPage/>} loader={authorLoader}/>
                    <Route path="create" element={<CreateAuthorPage/>} action={createAuthorAction}/>
                </Route>

                <Route path="*" element={<NotFoundPage/>}/>
            </Route>,
        ),
    )

    return <RouterProvider router={router}/>
}