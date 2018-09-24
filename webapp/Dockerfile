FROM node:8

WORKDIR /codesnippler_webapp

COPY package*.json ./

RUN npm install && npm install -g serve
COPY . .

ARG CLIENT_KEY
ARG CODE_SNIPPLER_BASEURL

RUN npm run build

ENTRYPOINT [ "serve", "-s" ]
CMD [ "build" ]
