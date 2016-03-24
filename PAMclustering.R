library(cluster) 

# use pam clustering with number of clusters from previous steps 
sbx <- pam(x = hugeMatrix, 10) 

# find center elements
medos <- as.data.frame(sbx$medoids) 

plot(sbx) 
