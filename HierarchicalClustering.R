library(NMFN) 
library(SpatioTemporal) 
library(gplots) 
library (RColorBrewer) 

# *********** Parameters ******************* # 


# weight for time penalty in the formula 
w <- 0.1

# number of latent features for SVD decomposition    
nlf <- 10  

# function for evaluation distance between vectors 
distfunction <- 'minkowski' 

# path to the file with results 
resultsFile <- 'data/openml_evaluations_all.csv' 

# path for images with plots 
savePath <- 'plots/'

# ****************************************** # 

# results of algorithms 
evaluations <- read.csv("data/openml_evaluations_all.csv")

source("codes/HugeMatrixFunctions.R") 

hugeMatrix <- createHugeMatrix(evaluations, 'task_id') 

hasEmptyValues <- TRUE  

if (hasEmptyValues) {
    # remove empty values 
    hugeMatrix <- hugeMatrix[, -37] 
    
    # impute new values 
    hugeMatrix <- SVDmiss(X = hugeMatrix, niter = 25, ncomp = dim(hugeMatrix)[2], conv.reldiff = 1E-3) 
    
    # make decomposition 
    hugeMatrixDecomposed <- svd(as.matrix(hugeMatrixImputed$Xfill), nlf, nlf) 
} else {
    hugeMatrixDecomposed <- svd(as.matrix(hugeMatrix), nlf, nlf)     
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

# heatmap for decomposed algorithms  
algDecomp <- drawPlot(t(hugeMatrixDecomposed$v), "algorithms_svd.png", width = 2048, height = 1536, 
         dendrogramType = "col", decomposed = TRUE, breakLen = 9, p.Rowv = NULL, p.Colv = TRUE, 
         p.cellNote = FALSE, p.cexRow = 2, p.cexCol = 1.2, p.margins = c(10, 10), 
         p.lmat = rbind(c(4, 2), c(0, 3), c(0, 1)), p.lhei = c(0.5, 2, 5), p.lwid = c(0.5, 3)) 

lowDimCols <- algDecomp$colInd 
lowDimCoden <- algDecomp$colDendrogram 


# heatmap for decomposed data sets with two dimensional dendrogram with flat top   
ds2Decomp <- drawPlot(hugeMatrixDecomposed$u, "datasets_svd_un.png", width = 3048, height = 2536, 
                      dendrogramType = "both", decomposed = TRUE, breakLen = 9, p.Rowv = TRUE, p.Colv = TRUE, 
                      p.cellNote = FALSE, p.cexRow = 0.8, p.cexCol = 2, p.margins = c(10, 10))  

# heatmap for decomposed algorithms two dimensional dendrogram with flat left structure 
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
 