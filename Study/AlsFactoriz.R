library(NMFN) 

ldwd <- getwd()

# set your own directory 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 

# perform ALS decomposition    
x.als <- nnmf(hugeMatrix, 11, 'nnmf_als')   
als_Wv <- x.als$W
als_Hv <- x.als$H   
 
#  start SVD method to look at results 
x.svd <- svd(hugeMatrix, 11, 11) 
svd_u <- x.svd$u 
svd_d <- x.svd$d 
svd_v <- x.svd$v 

# make clustering 
newSpcCls <- kmeans(als_Wv, 10) 
clusters <- data.frame(newSpcCls$cluster) 

# draw new clusters 
plot(als_Wv, col = newSpcCls$cluster, xlab = "Factor 1", ylab = "Factor 2", main = "Results of K-means clustering")   

# save new matrices in files 
write.csv(als_Wv, "W_of_ALS.csv") 
write.csv(als_Hv, "H_of_ALS.csv") 

setwd(oldwd) 

 