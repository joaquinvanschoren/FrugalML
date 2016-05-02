library(cluster) 

set.seed(123L) 

# use pam clustering with number of clusters from previous steps 
sbx <- pam(x = hugeMatrix, 11) 

# find center elements
medos <- as.data.frame(sbx$medoids) 

plot(sbx) 