import {Component, OnInit} from '@angular/core';
import {HorseService} from '../service/horse.service';
import {ToastrService} from 'ngx-toastr';
import {Owner} from '../dto/owner';
import {OwnerService} from '../service/owner.service';

@Component({
  selector: 'app-owner',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.scss']
})
export class OwnerComponent implements OnInit{
  owners: Owner[] = [];
  constructor(
    private service: OwnerService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadOwners();
  }

  reloadOwners() {
    console.log('Before getAll');
    this.service.getAll()
      .subscribe({
        next: data => {
          this.notification.success(`Owner data loaded sucessfuly`);
          this.owners = data;
        },
        error: error => {
          console.error('Error fetching owners', error);
          this.notification.error('Could Not Fetch Owners');
        }
      });
  }
}
