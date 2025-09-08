import copy from 'rollup-plugin-copy';

export default {
  input: 'electron/build/electron/src/index.js',
  output: [
    {
      file: 'electron/dist/plugin.js',
      format: 'cjs',
      sourcemap: true,
      inlineDynamicImports: true,
    },
  ],
  external: ['@capacitor/core'],
  plugins: [
    copy({
      targets: [{ src: 'electron/src/bin/Release/CapacitorArcGisMaps.exe', dest: 'electron/dist', rename: 'plugin.exe' }]
    })
  ]
};
