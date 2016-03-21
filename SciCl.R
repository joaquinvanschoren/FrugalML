library(NMFN) 
library(SpatioTemporal)
library(gplots)

# set your own directory 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 

# remove empty values
hugeMatrix <- hugeMatrix[, -37] 
hugeMatrix <- hugeMatrix[-75, ] 

# compute missed values
hugeMatrixImputed <- SVDmiss(X = hugeMatrix, niter = 25, ncomp = dim(hugeMatrix)[2], conv.reldiff = 1E-3) 

#  start SVD method to look at results 
x.svd <- svd(as.matrix(hugeMatrix), 20, 20) 
svd_u <- x.svd$u 
svd_d <- x.svd$d 
svd_v <- x.svd$v 

# save the number of important features for the next stages   
if (sum(svd_d > 3) > 20) {
    num_latent_features <- 20 
} else {
    num_latent_features <- sum(svd_d > 3) 
} 

x.svd <- svd(as.matrix(hugeMatrix), num_latent_features, num_latent_features) 
svd_u <- x.svd$u 

# perform ALS decomposition    
x.als <- nnmf(as.matrix(hugeMatrixImputed$Xfill), num_latent_features, 'nnmf_als')   
als_Wv <- x.als$W
als_Hv <- x.als$H 

# identify number of clusters with sum of squares with a cluster
# from http://www.statmethods.net/advstats/cluster.html   

mydata <- svd_u 
wss <- (nrow(mydata)-1)*sum(apply(mydata,2,var))
for (i in 2:500) wss[i] <- sum(kmeans(mydata,
                                     centers=i)$withinss)
plot(1:500, wss, type="b", xlab="Number of Clusters",
     ylab="Within groups sum of squares") 

library(cluster) 

# try anotehr way to identify a number of clusters 
time.scaled <- scale(hugeMatrix) 
k.max <- 15
data <- time.scaled
sil <- rep(0, k.max)

# Compute the average silhouette width for 
# k = 2 to k = 15
for(i in 2:k.max){
    km.res <- kmeans(data, centers = i, nstart = 25)
    ss <- silhouette(km.res$cluster, dist(data))
    sil[i] <- mean(ss[, 3])
}

# Plot the  average silhouette width
plot(1:k.max, sil, type = "b", pch = 19, frame = FALSE, xlab = "Number of clusters k")
abline(v = which.max(sil), lty = 2) 


# make clustering 
newSpcCls <- kmeans(mydata, 15) 
clusters <- data.frame(newSpcCls$cluster) 

# alias for the decomposed matrix   
hugeMatrixDecomposed <- x.svd 
 
# construct heat maps with heatmap function 
ds <- as.matrix(scale(hugeMatrix)) 
heatmap(ds, Colv = NA, Rowv = NULL, scale = 'none', distfun = function(x) dist(x,method = 'minkowski'), col = brewer.pal(8, "Accent")) 

algos <- as.matrix(scale(hugeMatrix)) 
heatmap(algos, Colv = T, Rowv = NA, scale = 'none', distfun = function(x) dist(x,method = 'minkowski')) 

# new function heatmap2 for data sets  
breaks_a <- seq(min(hugeMatrix), max(hugeMatrix), length.out = 9) 

png(filename= "plots/datasets.png", width = 6400, height = 4800) 
heatmap.2(as.matrix(hugeMatrix), 
    breaks = breaks_a, col = brewer.pal(8, "Set2"), keysize = 0.3, 
    Colv = NULL, density.info = "none", trace = "none", dendrogram = c("row"), 
    symm=F,symkey=F,symbreaks=T, 
    scale="none",  distfun = function(x) dist(x,method = 'minkowski'), 
    cellnote = as.matrix(round(hugeMatrix, 2)), 
    cexRow = 0.9, cexCol = 2, 
    margins = c(50,50)) 
dev.off() 

# heatmap for algorithms 
breaks_b <- seq(min(hugeMatrix), max(hugeMatrix), length.out = 9) 

png(filename= "plots/algorithms.png", width = 6400, height = 4800) 
heatmap.2(as.matrix(hugeMatrix), 
    breaks = breaks_b, col = brewer.pal(8, "Set2"), keysize = 0.3, 
    Rowv = NULL, density.info = "none", trace = "none", dendrogram = c("col"), 
    symm=F,symkey=F,symbreaks=T, 
    scale="none",  distfun = function(x) dist(x,method = 'minkowski'), 
    cellnote = as.matrix(round(hugeMatrix, 2)), 
    cexRow = 0.9, cexCol = 2, 
    margins = c(50,50)) 
dev.off() 

# heatmap2 for decomposed data sets  
breaks_c <- seq(min(hugeMatrixDecomposed$u), max(hugeMatrixDecomposed$u), length.out = 9) 

png(filename= "plots/datasets_svd.png", width = 3048, height = 2536) 
heatmap.2(as.matrix(hugeMatrixDecomposed$u), 
    breaks = breaks_c, col = brewer.pal(8, "Set2"), keysize = 0.3, 
    Colv = NULL, density.info = "none", trace = "none", dendrogram = c("row"), 
    symm=F,symkey=F,symbreaks=T, 
    scale="none",  distfun = function(x) dist(x,method = 'minkowski'), 
    cexRow = 0.8, cexCol = 2, 
    margins = c(10,10),
    lmat=rbind(c(4, 3, 0), c(0, 2, 1)), lhei=c(0.5, 5), lwid = c(0.5, 3, 3)) 
dev.off() 

# heatmap for decomposed algorithms 
breaks_d <- seq(min(hugeMatrixDecomposed$v), max(hugeMatrixDecomposed$v), length.out = 9) 

png(filename= "plots/algorithms_svd.png", width = 2048, height = 1536) 
heatmap.2(as.matrix(t(hugeMatrixDecomposed$v)), 
    breaks = breaks_d, col = brewer.pal(8, "Set2"), keysize = 0.3, 
    Rowv = NULL, density.info = "none", trace = "none", dendrogram = c("col"), 
    symm=F,symkey=F,symbreaks=T, 
    scale="none",  distfun = function(x) dist(x,method = 'minkowski'), 
    cexRow = 2, cexCol = 1.2, 
    margins = c(10,10),
    lmat=rbind(c(4, 2), c(0, 3), c(0, 1)), lhei=c(0.5, 2, 5), lwid = c(0.5, 3)) 
dev.off() 

# calculate principal components general function  
pcomponents <- prcomp(x = mydata, center = TRUE, scale = FALSE) 
names(pcomponents)  

# cumulative explained variance for each component   
png(filename= "plots/pca_first_variance.png", width = 800, height = 600) 
plot(cumsum(pcomponents$sdev^2/sum(pcomponents$sdev^2)), xlab = "Index of component", ylab = "Cumulative variance", main = "Explained variance") 
dev.off() 

# draw the two main components 
png(filename= "plots/pca_first.png", width = 1600, height = 1200) 
plot(pcomponents$x[,1], pcomponents$x[, 2], col = newSpcCls$cluster, xlab = "First component", ylab = "Second component", main = "PCA analysis")     
dev.off() 

library(readr)
library(Rtsne)

# The competition datafiles are in the directory ../input
# Read competition data files:

train <- data.frame(mydata)  
train$label <- matrix(newSpcCls$cluster, nrow = nrow(mydata), ncol = 1) 

train$name <- matrix(rownames(hugeMatrix), nrow = nrow(hugeMatrix), ncol = 1) 
train$name <- lapply(X = train$name, function(x) { 
    pos_un <- gregexpr("_", x, ignore.case = TRUE)[[1]][[1]] 
    x <- substr(x, 0, pos_un - 1) 
}) 
train$name <- as.numeric(train$name) 

# shrinking the size for the time limit
numTrain <- 517
set.seed(1)
rows <- sample(1:nrow(train), numTrain)
trainx <- train[ , c(-17)] 

# change every column with random numbers
noise <- scale(matrix(rexp(517 * 15, rate = 0.1), ncol = 15)) 
noise <- cbind(noise, matrix(rep(0, 517), ncol = 1)) 
trainx <- trainx + noise 
  
# create a new column with random generated values   
noise <- scale(matrix(rexp(517, rate = 0.1), ncol = 1)) 
trainx <- cbind(trainx, noise) 

# using tsne
set.seed(1) # for reproducibility 

tsne <- Rtsne(trainx, dims = 2, perplexity=30, check_duplicates = FALSE, verbose=TRUE, max_iter = 500) 

# visualizing
png(filename= "plots/tsne.png", width = 3200, height = 2500) 
colors = rainbow(length(unique(train$label)))
names(colors) = unique(train$label)
plot(tsne$Y, t='n', main="tsne")
text(tsne$Y, labels=train$name, col=colors[train$label], cex = 0.8) 
dev.off() 

# compare with pca
pca = princomp(train[,-1])$scores[,1:2] 
plot(pca, t='n', main="pca")
text(pca, labels=train$label,col=colors[train$label]) 
 