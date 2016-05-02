pcomponents <- prcomp(x = als_Wv, center = TRUE, scale = FALSE) 
names(pcomponents) 

#cumulative explained variance 
png(filename= "plots/pca_first_variance.png", width = 800, height = 600) 
plot(cumsum(pcomponents$sdev^2/sum(pcomponents$sdev^2)), xlab = "Index of component", ylab = "Cumulative variance", main = "Explained variance") 
dev.off() 

# draw the two main components 
png(filename= "plots/pca_first.png", width = 1600, height = 1200) 
plot(pcomponents$x[,1], pcomponents$x[, 2], col = newSpcCls$cluster, xlab = "First component", ylab = "Second component", main = "PCA analysis")     
dev.off()

# PCA via a covariance matrix - the eigenvalues now hold variance, not stdev 
png(filename= "plots/pca_variance_eigenvalues.png", width = 800, height = 600) 
plot(pcomponents$sdev^2, svd_d[1:11], xlab = "Variance", ylab = "Factor value", main = "Variance in eigenvalues") 
dev.off() 

# use hierarchical clustering 
hclustfunc <- function(x) hclust(x, method="complete")
distfunc <- function(x) as.dist((1-cor(t(x)))/2)
d <- distfunc(als_Wv)
fit <- hclustfunc(d)

dal <- distfunc(als_Hv)
fital <- hclustfunc(dal)

# save new dendrogram to a file  
png(filename= "plots/hierarchical_clustering_als.png", width = 8000, height = 600) 
plot(fit, xlab = "Data set name") 
dev.off() 

# one dimensional map      
dendcomplete <- as.dendrogram(fit)
heatmap(als_Wv, Rowv=dendcomplete, Colv=NA, scale="column")
 
# create two dimansional map 
dal <- distfunc(als_Hv)
fital <- hclustfunc(dal)
dendcompletealg <- as.dendrogram(fital) 

png(filename= "plots/heatmap_als.png", width = 7200, height = 4800)   

heatmap(als_Wv, Rowv = dendcomplete, Colv = dendcompletealg, margins = c(1,1), cexRow = 0.9, cexCol = 0.5) 

dev.off() 
