# Heroku Monorepo Deployment Guide

## Deployment

1. Create an app for backend: `heroku create aqyndar-backend-app --buildpack heroku/gradle --region eu --remote backend-remote`
2. Create an app for frontend: `heroku create aqyndar-frontend-app --buildpack heroku/nodejs --region eu --remote frontend-remote`
3. Get the web URL for the frontend app: `FRONTEND_URL=$(heroku info -s -a aqyndar-frontend-app | grep web_url | cut -d= -f2)`
4. Add the JWT keys and the frontend URL to the backend app: `heroku config:set JWT_ACCESS_SECRET= JWT_REFRESH_SECRET= CORS_ALLOWED_ORIGINS="http://localhost:5173,$FRONTEND_URL" -a aqyndar-backend-app`
5. Attach the postgres addon to the backend app: `heroku addons:create heroku-postgresql:mini -a aqyndar-backend-app`
6. Push the backend code to the backend app: `git subtree push --prefix backend backend-remote master`
7. Get the web URL for the backend app: `BACKEND_URL=$(heroku info -s -a aqyndar-backend-app | grep web_url | cut -d= -f2)`
8. Turn off the production profile for the frontend app: `heroku config:set NPM_CONFIG_PRODUCTION=false -a aqyndar-frontend-app`
9. Add the mode and the backend URL to the frontend app: `heroku config:set MODE=development VITE_API_BASE_URL=$BACKEND_URL -a aqyndar-frontend-app`
10. Push the frontend code to the frontend app: `git subtree push --prefix frontend frontend-remote master`

## Force Push

1. Create a temporary branch: `git subtree split --prefix=backend/frontend -b temp-branch`
2. Force push the temporary branch to the remote: `git push backend-remote temp-branch:master --force`
3. Delete the temporary branch: `git branch -D temp-branch`
