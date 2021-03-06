const path = require('path')

module.exports = {
	entry: [
		'./src/index'
	],
	output: {
		path: path.join(__dirname, 'public'),
		filename: 'bundle.js',
		publicPath: '/public/'
	},
	module: {
		loaders: [{
			exclude: /node_modules/,
			loader: 'babel',
			query: {
				presets: ['react', 'es2015', 'stage-1']
			}
		}]
	},
	resolve: {
		extensions: ['', '.js', '.jsx']
	},
	devServer: {
		historyApiFallback: true,
		contentBase: './public',
		publicPath: '/'
	}
}
