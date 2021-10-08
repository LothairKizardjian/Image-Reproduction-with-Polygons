This was a course project of my first MSc year. The goal was to reproduce an image using only up to 50 convex polygons (size, color, opacity can be whatever). I started from a set of 50 clusters of pixels close to one another in term of their color (if a pixel's color is too different from its neighboor it won't be part of the same cluster). I then applied Graham's march on each of the cluster of pixel to get 50 convex polygons. I then applied a sort of genetic algorithm on this set of polygons such that each iteration a random mutation is applied on one of the polygon (change in size, color, opacity, number of vertex etc ...) buy also making sure they remain convex. If the mutation produces a result closer to the original image it is kept, otherwise it is canceled.

This algorithm was successfull for small images (200 x 298 ~) but is far too slow for big images. The problem being the clustering of pixels which needs some improvements.

![image](https://user-images.githubusercontent.com/22938566/136579214-e3dcb08b-7c41-4c3a-83fc-05df130ed7dc.png)
![image](https://user-images.githubusercontent.com/22938566/136579233-924c3bef-897d-4757-921b-4dd99bc792cf.png)

![image](https://user-images.githubusercontent.com/22938566/136580439-23efc5e8-5b40-437a-b69f-b0782dd1924c.png)
![image](https://user-images.githubusercontent.com/22938566/136580457-c78e7d5d-9777-4038-b3ae-d59a6d57d4f0.png)


