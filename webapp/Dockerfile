FROM node:8

WORKDIR /codesnippler_webapp

COPY package*.json ./

RUN npm install && npm install -g serve
COPY . .

RUN npm run build

EXPOSE 3000
ENTRYPOINT [ "serve", "-s" ]
CMD [ "build" ]
