library(clValid) 
 
dunVals <- rep(NA, 19) 

for (i in 2:20) {
    clusters <- kmeans(mydata, i)$cluster 
    dunVals[i] <- dunn(Dist, clusters) 
} 

plot(dunVals) 
 