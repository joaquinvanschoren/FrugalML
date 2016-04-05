library(NMFN) 
library(SpatioTemporal) 
library(gplots) 
library (RColorBrewer) 
  
# set your own directory 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 

# function for evaluation distance between vectors 
distfunction <- 'euclidean' 

# path for images with plots 
savePath <- 'plots/'

# matrix has empty values 
hasEmptyValues <- sum(is.na(hugeMatrix)) != 0 

if (hasEmptyValues) {
    # remove empty values
    # hugeMatrix <- hugeMatrix[, -37] 
    # hugeMatrix <- hugeMatrix[-75, ] 
    
    # compute missed values
    hugeMatrixImputed <- SVDmiss(X = hugeMatrix, niter = 25, ncomp = dim(hugeMatrix)[2], conv.reldiff = 1E-3) 
    
    hugeMatrix <- hugeMatrixImputed$Xfill 
} 

#  start SVD method to look at results
nFeatures <- ncol(hugeMatrix)  
x.svd <- svd(as.matrix(hugeMatrix), nFeatures, nFeatures) 
svd_d <- x.svd$d 
plot(svd_d[1:nFeatures]) 

# save the number of important features for the next stages   
if (sum(svd_d > 3) > 20) {
    num_latent_features <- 20 
} else {
    num_latent_features <- sum(svd_d > 3) 
} 

# select number of features based on the image 
num_latent_features <- 20 

# compute the variance explained be selected number of features 
efv <- sum(svd_d[1:num_latent_features]) / sum(svd_d) 

# what method to use SVD or ALS for decomposition of original matrix 
enableALS <- FALSE 
 
if (enableALS) {
    # perform ALS decomposition    
    x.als <- nnmf(as.matrix(hugeMatrix), num_latent_features, 'nnmf_als') 
    als_Wv <- x.als$W
    als_Hv <- x.als$H 
    
    hugeMatrixDecomposed_d <- als_Wv 
    hugeMatrixDecomposed_a <- t(als_Hv) 
} else {
    x.svd <- svd(as.matrix(hugeMatrix), num_latent_features, num_latent_features) 
    hugeMatrixDecomposed_d <- x.svd$u 
    hugeMatrixDecomposed_a <- x.svd$v 
}

# scale columns in accordance with eigenvalues 
dataWeight <- t(t(hugeMatrixDecomposed_d) * svd_d[1:num_latent_features]) 

# identify number of clusters with sum of squares with a cluster
# from http://www.statmethods.net/advstats/cluster.html   
mydata <- hugeMatrixDecomposed_d 

useWeight <- TRUE 

if (useWeight) {
    numOfEigenvalues <- length(svd_d) 
    
    wss <- (nrow(mydata)-1)*sum(apply(mydata,2,var))
    for (i in 2:numOfEigenvalues) wss[i] <- sum(kmeans(mydata,
                                                       centers=i)$withinss * svd_d[1:i]) 
    plot(1:numOfEigenvalues, wss, type="b", xlab="Number of Clusters",
         ylab="Within groups sum of squares")     
} else {
    wss <- (nrow(mydata)-1)*sum(apply(mydata,2,var))
    for (i in 2:99) wss[i] <- sum(kmeans(mydata,
                                                       centers=i)$withinss) 
    plot(1:99, wss, type="b", xlab="Number of Clusters",
         ylab="Within groups sum of squares")        
}

library(cluster) 

# try another way to identify a number of clusters 
time.scaled <- scale(hugeMatrixDecomposed_d) 
k.max <- 25
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
newSpcCls <- kmeans(mydata, 10) 
clusters <- data.frame(newSpcCls$cluster) 

 
# construct heat maps with heatmap function 
ds <- as.matrix(scale(hugeMatrix)) 
heatmap(ds, Colv = NA, Rowv = NULL, scale = 'none', distfun = function(x) dist(x,method = 'minkowski'), col = brewer.pal(8, "Accent")) 

algos <- as.matrix(scale(hugeMatrix)) 
heatmap(algos, Colv = T, Rowv = NA, scale = 'none', distfun = function(x) dist(x,method = 'minkowski')) 


# use pam clustering with number of clusters from previous steps 
sbx <- pam(x = hugeMatrix, 11) 

# find center elements
medos <- as.data.frame(sbx$medoids) 


source("HugeMatrixFunctions.R") 

numOfIntervals <- 7 

# heatmap for data sets  
dsBig <- drawPlot(hugeMatrix, "datasets.png", width = 6400, height = 4800, 
                  dendrogramType = "row", p.distfunction = distfunction, 
                  decomposed = FALSE, breakLen = numOfIntervals, 
                  p.Rowv = TRUE, p.Colv = NULL, 
                  p.cellNote = TRUE)   

# heatmap for algorithms 
algBig <- drawPlot(hugeMatrix, "algorithms.png", width = 6400, height = 4800, 
                  dendrogramType = "col", p.distfunction = distfunction, 
                  decomposed = FALSE, breakLen = numOfIntervals, 
                  p.Rowv = NULL, p.Colv = TRUE, 
                  p.cellNote = TRUE)  

# heatmap for decomposed data sets  
dsDecomp <- drawPlot(hugeMatrixDecomposed_d, "datasets_svd.png", width = 3048, height = 2536, 
                     dendrogramType = "row", p.distfunction = distfunction, 
                     decomposed = TRUE, breakLen = numOfIntervals, p.Rowv = TRUE, p.Colv = NULL, 
                     p.cellNote = FALSE, p.cexRow = 0.8, p.cexCol = 2, p.margins = c(10, 10), 
                     p.lmat = rbind(c(4, 3, 0), c(0, 2, 1)), p.lhei=c(0.5, 5), p.lwid = c(0.5, 3, 3))  
  
lowDimRows <- dsDecomp$rowInd 
lowDimRoden <- dsDecomp$rowDendrogram 

# heatmap for decomposed algorithms  
algDecomp <- drawPlot(t(hugeMatrixDecomposed_a), "algorithms_svd.png", width = 2048, height = 1536, 
                      dendrogramType = "col", p.distfunction = distfunction, 
                      decomposed = TRUE, breakLen = numOfIntervals, p.Rowv = NULL, p.Colv = TRUE, 
                      p.cellNote = FALSE, p.cexRow = 2, p.cexCol = 1.2, p.margins = c(10, 10), 
                      p.lmat = rbind(c(4, 2), c(0, 3), c(0, 1)), p.lhei = c(0.5, 2, 5), p.lwid = c(0.5, 3)) 

lowDimCols <- algDecomp$colInd 
lowDimCoden <- algDecomp$colDendrogram 

bothBigMx <- drawPlot(hugeMatrix, "togetherMx.png", width = 6400, height = 4800, 
                      dendrogramType = "both", p.distfunction = distfunction, 
                      decomposed = FALSE, breakLen = numOfIntervals, 
                      p.Rowv = lowDimRoden, p.Colv = lowDimCoden, 
                      p.cellNote = FALSE) 

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
 
trainx <- train 

train$name <- matrix(rownames(hugeMatrix), nrow = nrow(hugeMatrix), ncol = 1) 
train$name <- lapply(X = train$name, function(x) { 
    pos_un <- gregexpr("_", x, ignore.case = TRUE)[[1]][[1]] 
    x <- substr(x, 0, pos_un - 1) 
}) 
train$name <- as.character(train$name) 

# create new data points with random generated values    
noiseLevel <- 1 
noise <- scale(matrix(rexp(dim(hugeMatrix)[1] * noiseLevel, rate = 0.1), ncol = noiseLevel)) 
train <- cbind(train, noise) 

# using tsne
set.seed(1) # for reproducibility 

tsne <- Rtsne(train, dims = 2, perplexity = 30, check_duplicates = FALSE, verbose = TRUE, max_iter = 500) 

# visualizing
png(filename= "plots/tsne.png", width = 3200, height = 2500) 
colors = rainbow(length(unique(train$label)))
names(colors) = unique(train$label)
plot(tsne$Y, t='n', main="tsne")
text(tsne$Y, labels=train$name, col=colors[train$label], cex = 0.8) 
dev.off() 

# compare with pca
pca = princomp(trainx)$scores[,1:2] 
plot(pca, t='n', main="pca")
text(pca, labels=train$label,col=colors[train$label]) 
 
