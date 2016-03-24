library(dbscan) 

sbx <- dbscan(hugeMatrix, eps = 1.5, minPts = 2) 

plot(sbx$cluster + 1L) 

