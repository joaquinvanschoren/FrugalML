library(NMFN) 
library(SpatioTemporal) 
library(gplots) 
library (RColorBrewer) 
library(cluster) 
library(Rtsne) 
library(tidyr)   
library(clustertend) 

imputeEmptyVals <- function(resMatrix) {
    # matrix has empty values 
    hasEmptyValues <- sum(is.na(resMatrix)) != 0 
    
    if (hasEmptyValues) {
        # remove empty values
        # hugeMatrix <- hugeMatrix[, -37] 
        # hugeMatrix <- hugeMatrix[-75, ] 
        
        # compute missed values
        srv() 
        resMatrixImputed <- SVDmiss(X = resMatrix, niter = 55, ncomp = dim(resMatrix)[2], conv.reldiff = 1E-3) 
        
        resMatrix <- resMatrixImputed$Xfill 
    }      
    
    return (resMatrix) 
}

makeSVDanalysis <- function(resMatrix, p.ALS = FALSE, p.numLatent = 20) { 
    # matrix has empty values 
    hasEmptyValues <- sum(is.na(resMatrix)) != 0 
    if (hasEmptyValues) {
        resMatrix <- imputeEmptyVals(resMatrix) 
        print("Imputed values were added to a matrix with empty results") 
    }
    
    #  start SVD method to look at results
    nFeatures <- ncol(resMatrix)  
    srv() 
    x.svd <- svd(as.matrix(resMatrix), nFeatures, nFeatures) 
    svd_d <- x.svd$d 
    plot(svd_d[1:nFeatures], xlab = "Number of a feature", ylab = "Importance of a feature") 
    
    # save the number of important features for the next stages   
    if (sum(svd_d > 3) > p.numLatent) {
        num_latent_features <- p.numLatent 
    } else {
        num_latent_features <- sum(svd_d > 3) 
    } 
    
    # select number of features based on the image 
    num_latent_features <- p.numLatent  
    
    # compute the variance explained be selected number of features 
    efv <- sum(svd_d[1:num_latent_features]) / sum(svd_d) 
    
    print(paste("Variance with ", num_latent_features, " latent features is ", efv, sep = "")) 
    
    # what method to use SVD or ALS for decomposition of original matrix 
    enableALS <- p.ALS 
    
    if (enableALS) {
        # perform ALS decomposition    
        x.tiny <- nnmf(as.matrix(resMatrix), num_latent_features, 'nnmf_als') 

        # als_Wv <- x.als$W
        # als_Hv <- x.als$H 
    } else {
        srv() 
        x.tiny <- svd(as.matrix(resMatrix), num_latent_features, num_latent_features) 
    } 
    
    return (x.tiny) 
} 

findSSE <- function(p.matrix, v.features, n.features = ncol(p.matrix), p.useWeights = FALSE, p.scaledFeatures = FALSE) {
    # scale columns in accordance with eigenvalues 
    svd_d <- v.features 
    
    if (p.scaledFeatures) { 
        # values of every feature scaled on a weight   
        dataWeight <- t(t(p.matrix) * svd_d[1:n.features]) 
    }

    # identify number of clusters with sum of squares with a cluster
    # from http://www.statmethods.net/advstats/cluster.html   
    numOfEigenvalues <- length(svd_d) - 1  
    if (p.useWeights) {
        wss <- (nrow(p.matrix)-1)*sum(apply(p.matrix,2,var))
        for (i in 2:numOfEigenvalues) { 
            srv() 
            wss[i] <- sum(kmeans(p.matrix, centers=i, nstart = 25, iter.max = 100)$withinss * svd_d[1:i]) 
        }
        plot(1:numOfEigenvalues, wss, type="b", xlab="Number of Clusters",
             ylab="Within groups sum of squares")     
    } else {
        wss <- (nrow(p.matrix)-1)*sum(apply(p.matrix,2,var))
        for (i in 2:numOfEigenvalues) { 
            srv() 
            wss[i] <- sum(kmeans(p.matrix, centers=i, nstart = 25, iter.max = 100)$withinss) 
        }
        plot(1:99, wss, type="b", xlab="Number of Clusters",
             ylab="Within groups sum of squares")        
    } 
    
} 
 
drawSilhouette <- function(p.matrix, p.k = 25, scale = TRUE) {
    # try another way to identify a number of clusters 
    if (scale) {
        time.scaled <- scale(p.matrix)  
    } else {
        time.scaled <- p.matrix 
    } 
    k.max <- p.k 
    sil <- rep(0, k.max)
    
    # Compute the average silhouette width for 
    # k = 2 to k = 15
    for(i in 2:k.max){
        srv() 
        km.res <- kmeans(time.scaled, centers = i, nstart = 25, iter.max = 100)
        ss <- silhouette(km.res$cluster, dist(time.scaled))     
        sil[i] <- mean(ss[, 3])
    }
    
    # Plot the  average silhouette width
    plot(1:k.max, sil, type = "b", pch = 19, frame = FALSE, xlab = "Number of clusters k")
    abline(v = which.max(sil), lty = 2) 
    
    maxValue <- which.max(sil) 
    
    return(maxValue) 
} 

getkMeansClusters <- function(p.matrix, p.numClusters, scale = TRUE) {
    if (scale) {
        p.matrix <- scale(p.matrix)  
    } 
    
    # make clustering 
    srv() 
    newSpcCls <- kmeans(p.matrix, p.numClusters, nstart = 25, iter.max = 100) 
    clusters <- newSpcCls$cluster 
    
    return (clusters) 
} 

getHierarchicalClusters <- function(p.dendrogram, p.numClusters) {
    # cut a tree from hierarchical clustering and compare with original splitting 
    hcLowRoden <- as.hclust(p.dendrogram)  
    cpDataSets  <- cutree(hcLowRoden, k = p.numClusters) 
    
    return (cpDataSets) 
}  
 
basicHeatMapsCreate <- function(p.matrix) { 
    # construct heat maps with heatmap function 
    ds <- as.matrix(scale(p.matrix)) 
    heatmap(ds, Colv = NA, Rowv = NULL, scale = 'none', distfun = function(x) dist(x,method = 'minkowski'), col = brewer.pal(8, "Accent")) 
    
    algos <- as.matrix(scale(p.matrix)) 
    heatmap(algos, Colv = T, Rowv = NA, scale = 'none', distfun = function(x) dist(x,method = 'minkowski')) 
} 

getMedoids <- function(p.matrix, p.numMedoids) { 
    # use pam clustering with number of clusters from previous steps 
    p.matrix <- scale(p.matrix) 
    srv() 
    sbx <- pam(x = p.matrix, p.numMedoids) 
    
    # find center elements
    medos <- as.data.frame(sbx$medoids) 
    
    return (medos) 
} 

createHeatMapsComplex <- function(p.matrix, p.dataSetsDecomposed, p.algorithmsDecomposed, p.distfunction = 'euclidean', p.savePath = 'plots/', p.numOfIntervals = 7) { 
    # function for evaluation distance between vectors 
    distfunction <- p.distfunction
    
    # path for images with plots 
    savePath <- p.savePath 
    
    # number of regions in a legend   
    numOfIntervals <- p.numOfIntervals  
    
    # heatmap for data sets  
    dsBig <- drawPlot(p.matrix, "datasets.png", width = 6400, height = 4800, 
                      dendrogramType = "row", p.distfunction = distfunction, 
                      decomposed = FALSE, breakLen = numOfIntervals, 
                      p.Rowv = TRUE, p.Colv = NULL, 
                      p.cellNote = FALSE)   
    
    # heatmap for algorithms 
    algBig <- drawPlot(p.matrix, "algorithms.png", width = 6400, height = 4800, 
                       dendrogramType = "col", p.distfunction = distfunction, 
                       decomposed = FALSE, breakLen = numOfIntervals, 
                       p.Rowv = NULL, p.Colv = TRUE, 
                       p.cellNote = FALSE)  
    
    # heatmap for decomposed data sets  
    dsDecomp <- drawPlot(p.dataSetsDecomposed, "datasets_svd.png", width = 3048, height = 2536, 
                         dendrogramType = "row", p.distfunction = distfunction, 
                         decomposed = TRUE, breakLen = numOfIntervals, p.Rowv = TRUE, p.Colv = NULL, 
                         p.cellNote = FALSE, p.cexRow = 0.8, p.cexCol = 2, p.margins = c(10, 10), 
                         p.lmat = rbind(c(4, 3, 0), c(0, 2, 1)), p.lhei=c(0.5, 5), p.lwid = c(0.5, 3, 3))  
    
    lowDimRows <- dsDecomp$rowInd 
    lowDimRoden <- dsDecomp$rowDendrogram 
    
    # heatmap for decomposed algorithms  
    algDecomp <- drawPlot(p.algorithmsDecomposed, "algorithms_svd.png", width = 2048, height = 1536, 
                          dendrogramType = "col", p.distfunction = distfunction, 
                          decomposed = TRUE, breakLen = numOfIntervals, p.Rowv = NULL, p.Colv = TRUE, 
                          p.cellNote = FALSE, p.cexRow = 2, p.cexCol = 1.2, p.margins = c(10, 10), 
                          p.lmat = rbind(c(4, 2), c(0, 3), c(0, 1)), p.lhei = c(0.5, 2, 5), p.lwid = c(0.5, 3)) 
    
    lowDimCols <- algDecomp$colInd 
    lowDimCoden <- algDecomp$colDendrogram 
    
    bothBigMx <- drawPlot(p.matrix, "togetherMx.png", width = 6400, height = 4800, 
                          dendrogramType = "both", p.distfunction = distfunction, 
                          decomposed = FALSE, breakLen = numOfIntervals, 
                          p.Rowv = lowDimRoden, p.Colv = lowDimCoden, 
                          p.cellNote = FALSE)     
    
    return (list(rows = lowDimRoden, cols = lowDimCoden))
}  

pcaPlots <- function(p.matrix, p.center = TRUE, p.scale = TRUE, p.savePath = 'plots/', p.clusters, p.alternative = FALSE) { 
    # calculate principal components general function  
    srv() 
    pcomponents <- prcomp(x = p.matrix, center = p.center, scale = p.scale) 
    names(pcomponents)  

    # cumulative explained variance for each component   
    png(filename= paste(p.savePath, "pca_first_variance.png", sep = ""), width = 800, height = 600) 
    plot(cumsum(pcomponents$sdev^2/sum(pcomponents$sdev^2)), xlab = "Index of component", ylab = "Cumulative variance", main = "Explained variance") 
    dev.off() 
    
    # draw the two main components 
    png(filename= paste(p.savePath, "pca_first.png", sep = ""), width = 800, height = 600) 
    plot(pcomponents$x[,1], pcomponents$x[, 2], col = p.clusters, xlab = "First component", ylab = "Second component", main = "PCA analysis")     
    dev.off() 
    
    if (p.alternative) { 
        
        train <- data.frame(p.matrix)  
        train$label <- matrix(p.clusters, nrow = nrow(p.matrix), ncol = 1) 
        
        # example from an external source   
        pca = princomp(train)$scores[,1:2] 
        plot(pca, t='n', main="pca")
        text(pca, labels = p.clusters, col = p.clusters) 
    }
} 

tsnePlot <- function(p.matrix, p.names, addName = TRUE, noiseLevel = 0, p.savePath = 'plots/', p.clusters, clusterAsFactor = TRUE) { 
    train <- data.frame(p.matrix)  
    
    # consider a number of cluster as a factor for visual analysis
    if (clusterAsFactor) { 
        train$label <- matrix(p.clusters, nrow = nrow(p.matrix), ncol = 1)      
        train$label <- as.factor(train$label) 
    } 

    if (noiseLevel > 0) {
        # create new data points with random generated values    
        noise <- scale(matrix(rexp(dim(p.matrix)[1] * noiseLevel, rate = 0.1), ncol = noiseLevel)) 
        train <- cbind(train, noise) 
    } 

    # usage of tSNE function from a corresponding package  
    srv()     
    tsne <- Rtsne(train, dims = 2, perplexity = 90, check_duplicates = FALSE, verbose = TRUE, max_iter = 1600) 

    if (addName) {
        train$name <- matrix(p.names, nrow = nrow(p.matrix), ncol = 1) 
        train$name <- lapply(X = train$name, function(x) { 
            pos_un <- gregexpr("_", x, ignore.case = TRUE)[[1]][[1]] 
            x <- substr(x, 0, pos_un - 1) 
        }) 
        train$name <- as.factor(as.character(train$name)) 
    }       
        
    # visualizing 
    png(filename= paste(p.savePath, "tsne.png", sep = ""), width = 800, height = 600) 
    if (clusterAsFactor) {
        colors = rainbow(length(unique(train$label))) 
        names(colors) = unique(train$label)
        plot(tsne$Y, t='n', main="tsne") 
        if (addName) {
            text(tsne$Y, labels=train$name, col=colors[train$label], cex = 0.8)                  
        } else {
            text(tsne$Y, labels=train$label, col=colors[train$label], cex = 0.8)              
        }
  
    } else {
        colors = rainbow(length(unique(p.clusters))) 
        names(colors) = unique(p.clusters)
        plot(tsne$Y, t='n', main="tsne") 
        if (addName) {
            text(tsne$Y, labels = train$name, col = colors[p.clusters], cex = 0.8)                   
        } else {
            text(tsne$Y, labels = p.clusters, col = colors[p.clusters], cex = 0.8)               
        }
    
    } 
    dev.off()        
} 
  
srv <- function() {
    set.seed(7L)     
}  
  