# Stage 1: Build Angular app
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source code
COPY . .

# Fix permissions for ng binary (Alpine-specific)
RUN chmod +x node_modules/.bin/ng

# Build Angular in production mode
RUN npm run build -- --configuration production
# Or: RUN npx ng build --configuration production

# Stage 2: Serve with NGINX
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist/angular-ui/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
