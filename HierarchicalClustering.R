library(NMFN) 
library(SpatioTemporal) 
library(gplots) 
library (RColorBrewer) 

# *********** Parameters ******************* # 


# weight for time penalty in the formula 
w <- 0.1

# number of latent features for SVD decomposition    
nlf <- 20 

# function for evaluation distance between vectors 
distfunction <- 'minkowski' 

# path to the file with results 
resultsFile <- 'data/openml_evaluations_all.csv' 

# path for images with plots 
savePath <- 'plots/'

# ****************************************** # 

# results of algorithms
evaluations <- read.csv(resultsFile)
separate_evaluations <- split(evaluations, evaluations$task_id)

# save names of data sets for further analysis 
allMeasures <- data.frame() 

# find all algrothms from all results
douplicatedAlgos <- evaluations$algo
algorithms <- data.frame(levels(douplicatedAlgos))

# prepare matrix with filling in the names of algorithms as column names
hugeMatrix <- data.frame(t(algorithms))
colnames(hugeMatrix) <- algorithms[1:103, 1]
hugeMatrix <- hugeMatrix[-1,] 

# store the number of algorithms in all items 
numOfAlgs <- dim(hugeMatrix)[2] 

# create short names 
colnames(algorithms) <- "algorithm" 
algorithms$algorithm <- as.character(algorithms$algorithm) 
for (i in 1: numOfAlgs) { 
    algFullName<- as.character(algorithms[i, 1])  
    
    posOfEmpty <- gregexpr(pattern =' ', algFullName)[[1]][1] 
    
    if (posOfEmpty > 0) {  
        shortName <- paste(substr(algFullName, 0, 5), substr(algFullName, 23, posOfEmpty - 1), sep = "") 
    } else {
        shortName <- paste(substr(algFullName, 0, 5), substr(algFullName, 23, nchar(algFullName)), sep = "") 
    }
    
    algorithms[i, 1] <- shortName 
} 
algorithms <- data.frame(t(algorithms))   

# change from factor to character 
algorithms[] <- lapply(algorithms, as.character) 

# replace long names with short names 
colnames(hugeMatrix) <- algorithms[1, ]  

# calculate the number of all sets in files 
numOfSets <- length(separate_evaluations)

for (i in 1:numOfSets) {
    # make a copy of set
    x <- as.data.frame(separate_evaluations[i])
    x <-
        setNames(
            x, c(
                "task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix"
            )
        )
    
    # receive name 
    xName <- as.character(x$dataset[1])
    allMeasures[i, 1] = xName    
    
    # change type from factor to numeric and remove NA values 
    x$auroc <- as.numeric(as.character(x$auroc))
    x <- x[!is.na(x$auroc),] 
    
    # convert time variable to numbers 
    x$training_millis <-
        as.numeric(as.character(x$training_millis))
    x$testing_millis <-
        as.numeric(as.character(x$testing_millis))
    
    x <- x[!is.na(x$training_millis),]
    x <- x[!is.na(x$testing_millis),]

    # caclulate and log combined time for each algorithm
    x$combineTime <- log(x$training_millis + x$testing_millis + 1)
    
    # filter columns and save AUC, training time and name of algorithm
    x <- data.frame(x$auroc, x$combineTime, x$algo)
    smallX <- setNames(x, c("AUC", "CombineTime", "Algorithm"))
    smallX <-
        smallX[order(smallX$AUC,smallX$CombineTime,decreasing = TRUE),]
    
    # create a vector of values for this set 
    paretoRes <- t(data.frame(rep(NA, numOfAlgs)))  
    colnames(paretoRes) <- colnames(hugeMatrix)  
    rownames(paretoRes) <- xName 
    
    for (algValue in 1:nrow(smallX)) { 
        algName <- as.character(smallX[algValue, 3])
        algIndex <- grep(pattern = substr(algName, 0, 5), x = colnames(paretoRes), ignore.case = TRUE) 
        paretoRes[1, algIndex] <- smallX[algValue, 1] - w * log(1 + smallX[algValue, 2]) 
    } 
    
    # add results to the matrix    
    hugeMatrix <- rbind(hugeMatrix, paretoRes) 
} 

# remove empty values 
hugeMatrix <- hugeMatrix[, -37] 

# impute new values 
hugeMatrixImputed <- SVDmiss(X = hugeMatrix, niter = 25, ncomp = dim(hugeMatrix)[2], conv.reldiff = 1E-3) 

# make decomposition 
hugeMatrixDecomposed <- svd(as.matrix(hugeMatrixImputed$Xfill), nlf, nlf) 

hugeMatrixDecomposed <- svd(as.matrix(hugeMatrix), nlf, nlf) 

# function for use advanced version of heatmap plot  
drawPlot <- function(p.matrix, fileName, 
                     width = 6400, height = 4800, dendrogramType, 
                     decomposed = FALSE, 
                     breakLen, p.Rowv = NULL, p.Colv = NULL, 
                     p.cellNote = FALSE, p.keysize = 0.3, 
                     p.lmat = NULL, p.lhei = NULL, p.lwid = NULL, 
                     p.cexRow = 0.9, p.cexCol = 2, p.margins = c(50, 50)) {

    # calculate number of intervals for a legend 
    
    if (decomposed) {
        breaks_s <- seq(quantile(p.matrix, 0.05, na.rm = TRUE), quantile(p.matrix, 0.95, na.rm = TRUE), length.out = breakLen) 
    } else {
        breaks_s <- seq(0, quantile(p.matrix, 0.95, na.rm = TRUE), length.out = breakLen - 1) 
        breaks_s <- c(-1, breaks_s) 
    }

    # create a file 
    png(filename = paste(savePath, fileName, sep = ""), width = width, height = height) 

    # check whether it is necessary to draw a vakue within a cell 
    if (p.cellNote) { 
        hm2res <- heatmap.2(as.matrix(p.matrix), 
                  breaks = breaks_s, col = brewer.pal(8, "YlOrRd"), keysize = p.keysize, 
                  Colv = p.Colv, Rowv = p.Rowv, density.info = "none", trace = "none", dendrogram = c(dendrogramType), 
                  symm=F,symkey=F,symbreaks=T, 
                  scale="none",  distfun = function(x) dist(x,method = distfunction), 
                  cellnote = as.matrix(round(p.matrix, 2)),  
                  cexRow = p.cexRow, cexCol = p.cexCol, 
                  margins = p.margins, 
                  lmat = p.lmat,
                  lhei = p.lhei, 
                  lwid = p.lwid) 
    } else {
        hm2res <- heatmap.2(as.matrix(p.matrix), 
                  breaks = breaks_s, col = brewer.pal(8, "YlOrRd"), keysize = p.keysize, 
                  Colv = p.Colv, Rowv = p.Rowv, density.info = "none", trace = "none", dendrogram = c(dendrogramType), 
                  symm=F,symkey=F,symbreaks=T, 
                  scale="none",  distfun = function(x) dist(x,method = distfunction), 
                  cexRow = p.cexRow, cexCol = p.cexCol, 
                  margins = p.margins, 
                  lmat = p.lmat,
                  lhei = p.lhei, 
                  lwid = p.lwid) 
    } 
    dev.off() 
    
    return (hm2res) 
} 
 
# heatmap for data sets  
dsBig <- drawPlot(hugeMatrix, "datasets.png", dendrogramType = "row", breakLen = 9, p.Rowv = TRUE, p.Colv = NULL, 
         p.cellNote = TRUE)   

# heatmap for algorithms 
algBig <- drawPlot(hugeMatrix, "algorithms.png", dendrogramType = "col", breakLen = 9, p.Rowv = NULL, p.Colv = TRUE, 
         p.cellNote = TRUE)  

# heatmap for original data set with two dimensional dendrogram 
bothBig <- drawPlot(hugeMatrix, "together.png", dendrogramType = "both", breakLen = 9, p.Rowv = TRUE, p.Colv = TRUE, 
         p.cellNote = FALSE) 

# heatmap for decomposed data sets  
dsDecomp <- drawPlot(hugeMatrixDecomposed$u, "datasets_svd.png", width = 3048, height = 2536, 
         dendrogramType = "row", decomposed = TRUE, breakLen = 9, p.Rowv = TRUE, p.Colv = NULL, 
         p.cellNote = FALSE, p.cexRow = 0.8, p.cexCol = 2, p.margins = c(10, 10), 
         p.lmat = rbind(c(4, 3, 0), c(0, 2, 1)), p.lhei=c(0.5, 5), p.lwid = c(0.5, 3, 3))  

lowDimRows <- dsDecomp$rowInd 
lowDimRoden <- dsDecomp$rowDendrogram 

# heatmap for decomposed data sets with two dimensional dendrogram   
ds2Decomp <- drawPlot(hugeMatrixDecomposed$u, "datasets_svd_un.png", width = 3048, height = 2536, 
         dendrogramType = "both", decomposed = TRUE, breakLen = 9, p.Rowv = TRUE, p.Colv = TRUE, 
         p.cellNote = FALSE, p.cexRow = 0.8, p.cexCol = 2, p.margins = c(10, 10))  

# heatmap for decomposed algorithms  
algDecomp <- drawPlot(t(hugeMatrixDecomposed$v), "algorithms_svd.png", width = 2048, height = 1536, 
         dendrogramType = "col", decomposed = TRUE, breakLen = 9, p.Rowv = NULL, p.Colv = TRUE, 
         p.cellNote = FALSE, p.cexRow = 2, p.cexCol = 1.2, p.margins = c(10, 10), 
         p.lmat = rbind(c(4, 2), c(0, 3), c(0, 1)), p.lhei = c(0.5, 2, 5), p.lwid = c(0.5, 3)) 

lowDimCols <- algDecomp$colInd 
lowDimCoden <- algDecomp$colDendrogram 

# heatmap for decomposed algorithms two dimensional dendrogram 
alg2Decomp <- drawPlot(t(hugeMatrixDecomposed$v), "algorithms_svd_un.png", width = 2048, height = 1536, 
          dendrogramType = "both", decomposed = TRUE, breakLen = 9, p.Rowv = TRUE, p.Colv = TRUE, 
          p.cellNote = FALSE, p.cexRow = 2, p.cexCol = 1.2, p.margins = c(10, 10)) 

hugeMatrixS <- hugeMatrix 

# hugeMatrixS$pos <- seq(1, 517) 
# hugeMatrixS <- hugeMatrixS[match(lowDimRows, hugeMatrixS$pos), ]  
# hugeMatrixS <- hugeMatrixS[, -104] 
# 
# hugeMatrixS[518, ] <- seq(1, 103) 
# hugeMatrixS <- hugeMatrixS[, match(lowDimCols, hugeMatrixS[518, ])] 
# hugeMatrixS <- hugeMatrixS[-518, ] 

bothBigMx <- drawPlot(hugeMatrix, "togetherMx.png", dendrogramType = "both", breakLen = 9, p.Rowv = lowDimRoden, p.Colv = lowDimCoden, 
                    p.cellNote = FALSE) 
 