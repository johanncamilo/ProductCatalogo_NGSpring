# ====== Build Angular ====== 
FROM node:22-alpine AS build 
WORKDIR /app 
COPY package*.json ./ 
RUN npm ci 
COPY . . 
RUN npm run build 
 
# ====== Nginx para servir SPA ====== 
FROM nginx:alpine 
COPY –from=build /app/dist/frontend-catalogo/browser /usr/share/nginx/html 
EXPOSE 80 
CMD ["nginx", "-g", "daemon off;"] 
 
 
