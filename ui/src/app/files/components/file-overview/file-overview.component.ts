/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, OnInit, ViewChild} from "@angular/core";
import {FilesService} from "../../../platform-services/apis/files.service";
import {FileMetadata} from "../../../core-model/gen/streampipes-model-client";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";
import {ConfirmDialogComponent} from "../../../core-ui/dialog/confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'file-overview',
  templateUrl: './file-overview.component.html',
  styleUrls: ['./file-overview.component.scss']
})
export class FileOverviewComponent implements OnInit {

  displayedColumns: string[] = ['filename', 'filetype', 'uploaded', 'action'];

  dataSource: MatTableDataSource<FileMetadata>;
  filesAvailable: boolean = false;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  pageSize: number = 1;

  constructor(private filesService: FilesService,
              private dialog: MatDialog) {

  }

  ngOnInit() {
    this.refreshFiles();
  }

  refreshFiles() {
    this.filesService.getFileMetadata().subscribe(fm => {
      this.dataSource = new MatTableDataSource<FileMetadata>(fm);
      this.filesAvailable = true;
      setTimeout(() => {
        this.dataSource.paginator = this.paginator;
      });
    });
  }

  deleteFile(fileMetadata: FileMetadata) {
    let dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '500px',
      data: {
        "title": "Do you really want to delete this file?",
        "subtitle": "This cannot be undone.",
        "cancelTitle": "No",
        "okTitle": "Yes",
        "confirmAndCancel": true
      },
    });

    dialogRef.afterClosed().subscribe(ev => {
      if (ev) {
        this.filesService.deleteFile(fileMetadata.fileId).subscribe(response => {
          this.refreshFiles();
        });
      }
    })
  }
}