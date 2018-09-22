FROM node:8

WORKDIR /codesnippler_webapp

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 8080
CMD [ "npm", "start" ]
